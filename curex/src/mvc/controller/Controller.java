package mvc.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import deserializer.RatesDeserializer;
import mvc.model.ApiResponse;
import mvc.model.RateObject;
import settings.Constants;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by michaelborisov on 20.02.17.
 */
public class Controller {


    public ApiResponse getRate(String baseCurrency, String toCurrency){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(RateObject.class, new RatesDeserializer())
                .create();
        File f = new File(String.format(Constants.CACHE_FILENAME, baseCurrency, toCurrency));

        if(f.exists() && !f.isDirectory() && !isUpdateNeeded(new Date(f.lastModified()), new Date())) {

            return getRateFromFile(f.getPath(), gson);

        }else{

            return getRateFomUrl(baseCurrency, toCurrency, gson);
        }
    }

    private ApiResponse getRateFromFile(String filePath, Gson gson){
        try {
            String text = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            ApiResponse resp = gson.fromJson(text, ApiResponse.class);
            return resp;
        }catch (IOException ioEx){
            //ioEx.printStackTrace();
            return null;
        }catch (Exception jsEx){
            jsEx.printStackTrace();
            File file = new File(filePath);
            boolean success = file.delete();
            return null;
        }
    }

    private ApiResponse getRateFomUrl(String baseCurrency, String toCurrency, Gson gson){

        try {
            URL mUrl = new URL(String.format(Constants.URL_ADDRESS, baseCurrency, toCurrency));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(mUrl.openStream())
            );

            String jsonResult = getStringFromBufferedReader(in);

            ApiResponse resp = gson.fromJson(jsonResult, ApiResponse.class);
            Controller.saveJsonToFile(
                    String.format(
                            Constants.CACHE_FILENAME,
                            resp.getBase(),
                            resp.getRates().getName()
                    ),
                    jsonResult);

            return resp;
        }catch (IOException ioEx){
            //ioEx.printStackTrace();
            return null;
        }
    }

    public static String getInput(String statement){
        Scanner reader = new Scanner(System.in);
        System.out.println(statement);
        String input = reader.next();
        return input;
    }

    public static String formatRateOutput(String fromCurrency, String toCurrency, ApiResponse resp){
        return String.format("%s => %s : %.3f", fromCurrency, toCurrency, resp.getRates().getRate());
    }

    public static void saveJsonToFile(String fileName, String jsonSrting){
        try(PrintWriter out = new PrintWriter(fileName)){
            out.print(jsonSrting);
        }catch (FileNotFoundException fnfEx){
            //fnfEx.printStackTrace();
        }
    }

    private static String getStringFromBufferedReader(BufferedReader in) {
        StringBuilder sb= new StringBuilder();
        String line = "";
        try {
            while (in.ready() && (line = in.readLine()) != null) {
                sb.append(line + "\r\n");
            }
            String result = sb.toString();
            return result;
        }catch (IOException ioEx){
            //ioEx.printStackTrace();
            return null;
        }


    }

    private boolean isUpdateNeeded(Date first, Date second){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(first);

        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(second);
        if (cal1 == null || cal2 == null)
            return false;
        return !(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

}
