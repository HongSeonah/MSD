package com.example.cafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OrderActivity extends AppCompatActivity {
    private TextView tvDrink1Quantity, tvDrink2Quantity, tvDrink3Quantity, tvTotalPrice;
    private Button btnDrink1Decrease, btnDrink1Increase, btnDrink2Decrease, btnDrink2Increase,
            btnDrink3Decrease, btnDrink3Increase, btnOrder;
    private int drink1Price = 5000, drink2Price = 7000, drink3Price = 4500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize Views
        tvDrink1Quantity = findViewById(R.id.tvDrink1Quantity);
        tvDrink2Quantity = findViewById(R.id.tvDrink2Quantity);
        tvDrink3Quantity = findViewById(R.id.tvDrink3Quantity);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnDrink1Decrease = findViewById(R.id.btnDrink1Decrease);
        btnDrink1Increase = findViewById(R.id.btnDrink1Increase);
        btnDrink2Decrease = findViewById(R.id.btnDrink2Decrease);
        btnDrink2Increase = findViewById(R.id.btnDrink2Increase);
        btnDrink3Decrease = findViewById(R.id.btnDrink3Decrease);
        btnDrink3Increase = findViewById(R.id.btnDrink3Increase);

        // Set onClickListeners
        btnDrink1Decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity(tvDrink1Quantity);
            }
        });

        btnDrink1Increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity(tvDrink1Quantity);
            }
        });

        btnDrink2Decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity(tvDrink2Quantity);
            }
        });

        btnDrink2Increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity(tvDrink2Quantity);
            }
        });

        btnDrink3Decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity(tvDrink3Quantity);
            }
        });

        btnDrink3Increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity(tvDrink3Quantity);
            }
        });

        btnOrder = findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    // Method to decrease quantity
    private void decreaseQuantity(TextView tvQuantity) {
        int quantity = Integer.parseInt(tvQuantity.getText().toString());
        if (quantity > 0) {
            quantity--;
            tvQuantity.setText(String.valueOf(quantity));
            calculateTotalPrice();
        }
    }

    // Method to increase quantity
    private void increaseQuantity(TextView tvQuantity) {
        int quantity = Integer.parseInt(tvQuantity.getText().toString());
        quantity++;
        tvQuantity.setText(String.valueOf(quantity));
        calculateTotalPrice();
    }

    // Method to calculate total price
    private void calculateTotalPrice() {
        int drink1Quantity = Integer.parseInt(tvDrink1Quantity.getText().toString());
        int drink2Quantity = Integer.parseInt(tvDrink2Quantity.getText().toString());
        int drink3Quantity = Integer.parseInt(tvDrink3Quantity.getText().toString());

        int totalPrice = (drink1Price * drink1Quantity) + (drink2Price * drink2Quantity) + (drink3Price * drink3Quantity);
        tvTotalPrice.setText("총 가격: " + totalPrice + "원");
    }

    private void placeOrder() {
        // Display toast message
        Toast.makeText(this, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show();

        // Reset quantity to 0
        tvDrink1Quantity.setText("0");
        tvDrink2Quantity.setText("0");
        tvDrink3Quantity.setText("0");

        // Recalculate total price
        calculateTotalPrice();
    }
}
