package mvc.view;

import mvc.controller.Controller;
import mvc.model.ApiResponse;
import settings.Constants;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;


/**
 * Created by michaelborisov on 20.02.17.
 */
public class Main {

    /*
    Все .printStackTrace() были закомментированы, так как в задании сказано: "пользователь не должен
    получать исключений". Считаю, что нужно оставить, но задание не позволяет.
     */
    public static void main(String[] args) {
        while(true) {
            final String baseCurrency = Controller.getInput(Constants.STATEMENT_FOR_BASE);
            final String toCurrancy = Controller.getInput(Constants.STATEMENT_FOR_RATE);
            RunnableFuture<ApiResponse> runnableFuture = startAsyncWork(baseCurrency, toCurrancy);
            ApiResponse result = drawProgressAndResult(runnableFuture);
            if (result == null) {
                System.out.println("\n" + "Oops, something went wrong. Please, check your input and try again." + "\n");
            } else {
                System.out.println("\n" + Controller.formatRateOutput(baseCurrency, toCurrancy, result));
            }
        }
    }

    public static RunnableFuture<ApiResponse> startAsyncWork(final String baseCurrency, final String toCurrancy){
        RunnableFuture<ApiResponse> f = new FutureTask<ApiResponse>(new Callable<ApiResponse>() {
            @Override
            public ApiResponse call() throws Exception {
                Controller mController = new Controller();
                ApiResponse response = mController.getRate(baseCurrency, toCurrancy);
                return response;
            }
        });

        new Thread(f).start();
        return f;
    }

    public static ApiResponse drawProgressAndResult(RunnableFuture<ApiResponse> runnableFuture){
        try {
            while(!runnableFuture.isDone()){
                Thread.sleep(Constants.TIMEOUT);
                System.out.print(".");
            }
            return runnableFuture.get();
        }catch (InterruptedException iEx){
            //iEx.printStackTrace();
            return null;
        }catch (ExecutionException eEX){
            //eEX.printStackTrace();
            return null;
        }
    }
}
