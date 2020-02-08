package compte.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import compte.model.Categorie;
import compte.model.Depense;
import compte.services.DaoServices;

public class DaoServicesImpl implements DaoServices {

	private AmazonDynamoDB client;
	private DynamoDBMapper mapper;
	private DynamoDB dynamoDB;

	public DaoServicesImpl() {
		super();
		client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_3).build();
		mapper = new DynamoDBMapper(client);
		dynamoDB = new DynamoDB(client);
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
		
		
		Map<String, AttributeValue> expressionAttributeValues = 
			    new HashMap<String, AttributeValue>();
			expressionAttributeValues.put(":lcategorie", new AttributeValue().withS(categorie)); 
			expressionAttributeValues.put(":startdate", new AttributeValue().withN(""+calStart.getTime().getTime())); 
			expressionAttributeValues.put(":enddate", new AttributeValue().withN(""+calEnd.getTime().getTime())); 
			        
			ScanRequest scanRequest = new ScanRequest()
			    .withTableName("Depenses")
			    .withFilterExpression("categorie = :lcategorie and datedepense between :startdate and :enddate")
			    .withExpressionAttributeValues(expressionAttributeValues);

			List<Depense> allDepense = new ArrayList<Depense>();
			ScanResult result = client.scan(scanRequest);
			for (Map<String, AttributeValue> item : result.getItems()) {
			  Depense temp = new Depense();
			  temp.setCategorie(item.get("categorie").getS());
			  temp.setDate(Long.parseLong(item.get("datedepense").getN()));
			  temp.setMontant(Double.parseDouble(item.get("montant").getN()));
			  allDepense.add(temp);
			}
		
		return allDepense;
		
	}

	@Override
	public List<Categorie> getCategories() {
		return mapper.scan(Categorie.class, new DynamoDBScanExpression());
	}

}
