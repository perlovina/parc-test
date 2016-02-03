package org.perlovka.parc.offer;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Random;

/**
 * @author Ivan Kaplin
 */
public class OfferGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(OfferGenerator.class);

  public static OfferGenerator get() {
    return new OfferGenerator();
  }

  public void sendOffer(String offer) {
    try {
      String url = "http://offers-parc.cogniance.com:8080/offers/admin";

      CloseableHttpClient client = HttpClients.createDefault();
      HttpPost post = new HttpPost(url);

      StringEntity input = new StringEntity(offer);
      input.setContentType("application/json");
      post.setEntity(input);


      CloseableHttpResponse response = client.execute(post);
      if (response.getStatusLine().getStatusCode() != 200) {
       BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result=new StringBuffer();
        String line="";
        while((line=rd.readLine())!=null){
          result.append(line);
        }


        System.out.println(result.toString());

        throw new RuntimeException("Failed : HTTP error code : "
                                   + response.getStatusLine().getStatusCode());

      }
      try{
        System.out.println("\nSending 'POST' request to URL : "+url);
        System.out.println("Post parameters : "+post.getEntity());
        System.out.println("Response Code : "+
                           response.getStatusLine().getStatusCode());

        BufferedReader rd=new BufferedReader(
            new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result=new StringBuffer();
        String line="";
        while((line=rd.readLine())!=null){
          result.append(line);
        }

        System.out.println(result.toString());
      }finally {
        response.close();
        client.close();
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static final String OFFER_START = "1451606400000";
  private static final String OFFER_END = "1467331200000";

  public JSONObject generateOffer(Long merchant, Long category){
    String[] weekDays = generateWeekDays();

    String[] mDayPart = getDayPart();
    String text = "Offer with category: "+ category + "and dayPart = " + mDayPart.toString() + "and dayWeek: " + weekDays.toString();

    JSONObject obj = new JSONObject();

    obj.put("text",text);
    obj.put("offerType", getOfferType());
    obj.put("quantity", 1000);
    obj.put("minCostToApply", 39.99);
    obj.put("itemBonusShortText", null );
    obj.put("savings", 43);
    obj.put("imageLocation","https://img.grouponcdn.com/deal/ch53eiVdHsQfwqYUQwcb/NP-2048x1229/v1/c312x189.jpg");
    obj.put("activationInstant", OFFER_START);
    obj.put("expirationInstant", OFFER_END);

    JSONArray daysOfWeek = new JSONArray();
    for (String dayOfWeek : weekDays) {
      daysOfWeek.add(dayOfWeek);
    }
    obj.put("daysOfWeek", daysOfWeek);
    obj.put("startLocalTime", "09:00");
    obj.put("endLocalTime", "23:00");
    JSONArray dayParts = new JSONArray();
    for (String dayPart : mDayPart) {
      dayParts.add(dayPart);
    }
    obj.put("dayParts", dayParts);

    JSONObject merchant1 = new JSONObject();
    merchant1.put("id", merchant);
    obj.put("merchant", merchant1);


    JSONArray Interest = new JSONArray();

    JSONObject idInterest = new JSONObject();
    idInterest.put("id", getInterest());
    idInterest.put("id", getInterest());
    Interest.add(idInterest);
    obj.put("interests", Interest);

    return obj;
  }

  private String[] dayPart = {
      "Morning","Daytime","Evening"
  };

  private String[] getDayPart(){
    return dayPart;
  }

  private static final String[][] weekDaysOptions = {
      {"MONDAY", "TUESDAY", "WEDNESDAY"},
      {"THURSDAY", "FRIDAY", "SATURDAY"},
      {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"}
  };

  private int weekDaysCounter = 0;
  private String[] generateWeekDays() {
    return weekDaysOptions[weekDaysCounter++ % 3];
  }

  private boolean offerType = false;
  private String getOfferType() {
    offerType = !offerType;
    return offerType ? "PercentageDiscount" : "MoneyOff";
  }

  private static final Random random = new Random();
  private int getInterest() {
    return random.nextInt(14) + 1;
  }

}
