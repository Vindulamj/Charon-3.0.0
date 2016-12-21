package org.wso2.charon.samples.group.sample03;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ListGroupsSample {

    public static void main(String[] args) {

        try {
            String url = "http://localhost:8080/scim/v2/Groups";
            URL obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();


            BufferedReader in;
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(
                        con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //printing result from response
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Message : " + con.getResponseMessage());
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(response.toString(), Object.class);
            System.out.println("Response Content : " +
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
