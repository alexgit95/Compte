package compte.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import compte.model.Categorie;
import compte.model.Depense;
import compte.services.DaoServices;
import compte.services.impl.DaoServicesImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;

public class MainVerticle extends AbstractVerticle {

	private DaoServices daoServices;
	private final Logger log = LoggerFactory.getLogger( MainVerticle.class );
	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		
		daoServices= new DaoServicesImpl();
		
		final Router router = Router.router(vertx);

		
		
		router.route()
				.handler(CorsHandler.create("*").allowedMethod(io.vertx.core.http.HttpMethod.GET)
						.allowedHeader("Access-Control-Request-Method")
						.allowedHeader("Access-Control-Allow-Credentials").allowedHeader("Access-Control-Allow-Origin")
						.allowedHeader("Access-Control-Allow-Headers").allowedHeader("Content-Type"));

		router.post("/api/v1/compte/depense/:montant/:categorie").handler(this::createDepense);
		router.get("/api/v1/compte/depense/:categorie").handler(this::getDepenseByCurrentMonthAndCategorie);
		router.get("/api/v1/compte/depense/:categorie/:month/:year").handler(this::getDepenseByMonthAndCategorie);
		router.get("/api/v1/compte/categories").handler(this::getCategories);
		
		vertx.createHttpServer().requestHandler(router).listen(8877, res -> {
			if (res.succeeded()) {
				log.info("Serveur demarre sur le port " + 8877);
				startPromise.complete();
			} else {
				startPromise.fail(res.cause());
			}
		});

	}

	private void createDepense(RoutingContext ctx) {
		Depense dep = new Depense();
		dep.setCategorie(ctx.request().getParam("categorie"));
		dep.setDate(dateFormatter.format(new Date()));
		dep.setMontant(Double.parseDouble(ctx.request().getParam("montant")));
		
		
		try {
			log.info("Insertion de "+dep.toString());
			daoServices.insert(dep);
			outputOk(ctx);
		} catch (Exception e) {
			outputError(ctx, e);
		}

	}
	
	private void getDepenseByCurrentMonthAndCategorie(RoutingContext ctx) {
		Date currentDate= new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		List<Depense> depenseByMonth = daoServices.getDepenseByMonth(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), ctx.request().getParam("categorie"));
		
		getJSONResponse(ctx).end(new JsonObject().put("result", depenseByMonth).encodePrettily());
	}
	
	private void getDepenseByMonthAndCategorie(RoutingContext ctx) {
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(ctx.request().getParam("year")), Integer.parseInt(ctx.request().getParam("month")), 1);
		List<Depense> depenseByMonth = daoServices.getDepenseByMonth(cal.get(Calendar.MONTH),  cal.get(Calendar.YEAR), ctx.request().getParam("categorie"));
		
		getJSONResponse(ctx).end(new JsonObject().put("result", depenseByMonth).encodePrettily());
	}
	
	private void getCategories(RoutingContext ctx) {
		List<Categorie> all = daoServices.getCategories();
		getJSONResponse(ctx).end(new JsonObject().put("result", all).encodePrettily());
	}

	
	
	
	
	private void outputError(RoutingContext ctx, Exception e) {
		log.error("Erreur : ", e);
		e.printStackTrace();
		getJSONResponse(ctx)
				.end(new JsonObject().put("status", "ko").put("msg", e.getMessage()).encodePrettily());
	}
	
	private void outputOk(RoutingContext ctx) {
		getJSONResponse(ctx).end(new JsonObject().put("status", "ok").encodePrettily());
	}
	

	private HttpServerResponse getJSONResponse(RoutingContext ctx) {
		return ctx.response().putHeader("Content-Type", "application/json");
	}
	
	
}
