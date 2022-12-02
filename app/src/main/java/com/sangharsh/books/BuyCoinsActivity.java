package com.sangharsh.books;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.common.collect.ImmutableList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.gson.Gson;
import com.sangharsh.books.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BuyCoinsActivity extends AppCompatActivity {
    private PurchasesUpdatedListener purchasesUpdatedListener;
    private BillingClient billingClient;
    private Boolean isBillingOpen;
    private HashMap<String, Integer> buttons;
    private HashMap<String, String> coins;
    private HashMap<String, ProductDetails> skuDetails;
    private Boolean handlingPurchase;
    private HashMap<String, Integer> points;
    private ProgressBar progressBar;
    private Boolean inNormalState;
    private Boolean purchasedSomething;

    @Override
    public void onDestroy() {
        billingClient.endConnection();
        super.onDestroy();
    }

    private void generateHasMapsOfData(){
        buttons = new HashMap<String, Integer>();
        buttons.put("coins_1500", R.id.coins_1500);
        buttons.put("coins_5000", R.id.coins_5000);
        buttons.put("coins_10000", R.id.coins_10000);

        coins = new HashMap<String, String>();
        coins.put("coins_1500", "1,500 coins for\n\n");
        coins.put("coins_5000", "5,000 coins for\n\n");
        coins.put("coins_10000", "10,000 coins for\n\n");

        points = new HashMap<String, Integer>();
        points.put("coins_1500", 1500);
        points.put("coins_5000",5000);
        points.put("coins_10000", 10000);

        skuDetails = new HashMap<String, ProductDetails>();
    }

    private void setInitialConditions(){
        isBillingOpen = false;
        handlingPurchase = false;
        inNormalState = true;
        purchasedSomething = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coins);

        setInitialConditions();
        generateHasMapsOfData();

        progressBar = findViewById(R.id.progressBar);

        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && list != null) {
                    for (Purchase purchase : list) {
                        handlePurchase(purchase);
                    }
                } else {
                    inNormalState = true;
                }
            }
        };
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        Log.i("Billing", "Establishing Connection");


        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.i("Billing", "Connection successfully established");
                    isBillingOpen = true;
                    querryPrices();
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){
                    showBillingNotAvailableDialog();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                isBillingOpen = false;
                billingClient.startConnection(this);
                Log.i("Billing", "Connection failed");
            }
        });
    }

    private void showBillingNotAvailableDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Billing Not Available")
                .setMessage("We are afraid to let you know that in-app purchase is not available for your device.\n" +
                        "This is due to one or many of these reasons:\n" +
                        "1. The Play Store app on the user's device is out of date.\n" +
                        "2. The user is in an unsupported country.\n" +
                        "3. The user is an enterprise user and their enterprise admin has disabled users from making purchases.\n" +
                        "4. Google Play is unable to charge the user’s payment method.")
                .setCancelable(false)
                .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        BuyCoinsActivity.this.finish();
                    }
                })
                .show();
    }

    private void querryPrices() {
        List<String> skuList = new ArrayList<>();
        skuList.add("coins_1500");
        skuList.add("coins_5000");
        skuList.add("coins_10000");
        List<QueryProductDetailsParams.Product> products = new ArrayList<>();
        for (String sku : skuList){
            products.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(sku)
                    .setProductType(BillingClient.ProductType.INAPP).build());
        }
        QueryProductDetailsParams productParams = QueryProductDetailsParams.newBuilder()
                .setProductList(products)
                .build();
        Log.i("Billing", "Started Query");
        billingClient.queryProductDetailsAsync(productParams,
                new ProductDetailsResponseListener() {
                    @Override
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                     List<ProductDetails> productDetails) {
                        Log.i("Billing", "Got response");
                        for (ProductDetails x : productDetails) {
                            Log.i("Billing", "Setting Txt");
                            skuDetails.put(x.getProductId(), x);
                            MaterialButton button = findViewById(buttons.get(x.getProductId()));
                            Log.i("Billing: ", new Gson().toJson(x));
                            String text = x.getName() + "\n" + Objects.requireNonNull(x.getOneTimePurchaseOfferDetails()).getFormattedPrice();
                            Log.i("Billing", "generated: " + text);
                            button.setText(text);
                            Log.i("Billing", "text set");
                        }
                        Log.i("Billing", "out of loop");
                    }
                });
    }

    public void buyCoins(View view) {
        if (!handlingPurchase) {
            inNormalState = false;
            String id = (String) view.getTag();
            if (billingClient != null && skuDetails.size() > 0 && skuDetails.containsKey(id)) {
                ImmutableList productDetailsParamsList =
                        ImmutableList.of(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(skuDetails.get(id))
                                        .build()
                        );

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build();
                int responseCode = billingClient.launchBillingFlow(this, billingFlowParams).getResponseCode();
                if (responseCode != BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(this, "Some error occurred! Please try again", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Let the prices load first, then try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please wait while we credit your coins", Toast.LENGTH_SHORT).show();
        }
    }
    private int retryTime = 0 ;

    private void handlePurchase(final Purchase purchase) {
        if (purchase!=null && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
            progressBar.setVisibility(View.VISIBLE);
            handlingPurchase = true;
            int totalPointsAdded = 0;
            for (String sku: purchase.getProducts()){
                totalPointsAdded = totalPointsAdded + points.get(sku);
            }
            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("points")
                    .setValue(ServerValue.increment(totalPointsAdded))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                User.refresh();
                                retryTime = 0;
                                purchasedSomething = true;
                                inNormalState = true;
                                final ConsumeParams consumeParams =
                                        ConsumeParams.newBuilder()
                                                .setPurchaseToken(purchase.getPurchaseToken())
                                                .build();

                                ConsumeResponseListener listener = new ConsumeResponseListener() {
                                    @Override
                                    public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                            handlingPurchase = false;
                                            retryTime = 0;
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(BuyCoinsActivity.this, "Purchase successful!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (retryTime >2){
                                                handlingPurchase = false;
                                                retryTime = 0;
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(BuyCoinsActivity.this, "\"Something went wrong! You will be refunded in 3 days if we do not credit points.\"", Toast.LENGTH_SHORT).show();
                                            } else {
                                                retryTime++;
                                                billingClient.consumeAsync(consumeParams, this);
                                            }
                                        }
                                    }
                                };

                                billingClient.consumeAsync(consumeParams, listener);
                            } else {
                                if (retryTime >2){
                                    handlingPurchase = false;
                                    retryTime = 0;
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(BuyCoinsActivity.this, "\"Something went wrong! You will be refunded in 3 days if there was any deductions which were not meant to.\"", Toast.LENGTH_SHORT).show();
                                } else {
                                    retryTime++;
                                    handlePurchase(purchase);
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Something went wrong! You will be refunded in 3 days if there was any deductions which were not meant to.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (!handlingPurchase) {
            if (inNormalState){
                if (purchasedSomething){
                    setResult(RESULT_OK);
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            } else {
                super.onBackPressed();
            }
        } else {
            Toast.makeText(this, "Please let the transaction complete first", Toast.LENGTH_SHORT).show();
        }
    }
}