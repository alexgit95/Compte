package compte.services.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import compte.api.MainVerticle;
import compte.model.Categorie;
import compte.model.Depense;
import compte.services.DaoServices;

public class DaoServicesImpl implements DaoServices {

	private AmazonDynamoDB client;
	private DynamoDBMapper mapper;

	public DaoServicesImpl() {
		super();
		client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_3).build();
		mapper = new DynamoDBMapper(client);
	}
	
	@Override
	public void insert(Depense toCreate) {
		mapper.save(toCreate);
		
	}

	@Override
	public List<Depense> getDepenseByMonth(int month, int year, String categorie) {
		Calendar calStart = Calendar.getInstance();
		calStart.set(year, month, 1, 0, 0, 0);
		
		Calendar calEnd = Calendar.getInstance();
		calEnd.set(year, month, calEnd.getActualMaximum(Calendar.DATE), 23, 59, 59);
		
		String startDate = MainVerticle.dateFormatter.format(calStart.getTime());
		String endDate = MainVerticle.dateFormatter.format(calEnd.getTime());

		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":start", new AttributeValue().withS(startDate));
		eav.put(":end", new AttributeValue().withS(endDate));
		eav.put(":categorie", new AttributeValue().withS(categorie));

		DynamoDBQueryExpression<Depense> queryExpression = new DynamoDBQueryExpression<Depense>()
				.withKeyConditionExpression("date between :start and :end and categorie=:categorie")
				.withExpressionAttributeValues(eav);

		return mapper.query(Depense.class, queryExpression);
	}

	@Override
	public List<Categorie> getCategories() {
		return mapper.scan(Categorie.class, new DynamoDBScanExpression());
	}

}
