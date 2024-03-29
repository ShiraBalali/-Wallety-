package com.example.wallety;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.example.wallety.adapters.HomeAdapter;
import com.example.wallety.databinding.FragmentHomeBinding;
import com.example.wallety.databinding.FragmentLinkCardBinding;
import com.example.wallety.model.CreditCard;
import com.example.wallety.model.Model;
import com.example.wallety.model.Transactions;
import com.example.wallety.model.User;
import com.example.wallety.model.server.AccessTokenRequest;
import com.example.wallety.model.server.LinkCardRequest;
import com.example.wallety.model.server.UserFetcherCon;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    RecyclerView recyclerView;
    List<Transactions> transactionsList;
    HomeAdapter homeAdapter;

    private View partialView;
    private TextView creditCardCode, creditCardHolder, creditCardMonth, creditCardYear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        String nameHeader = "Hello " + Model.instance().getCurrentUser().getName();
        binding.nameHeaderTv.setText(nameHeader);

        partialView = view.findViewById(R.id.partial);
        creditCardCode = view.findViewById(R.id.code);
        creditCardHolder = view.findViewById(R.id.holder);
        creditCardMonth = view.findViewById(R.id.month1);
        creditCardYear = view.findViewById(R.id.year1);

        binding.linkCardCv.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.action_homeFragment_to_linkCardFragment);
        });

        binding.savingMoneyCv.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.action_homeFragment_to_savingMoneyFragment);
        });

        binding.transferMoneyCv.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.action_homeFragment_to_stripeActivity);
        });

        User user = Model.instance().getCurrentUser();

        if (user.isParent()) {
            binding.unusualExpensesCv.setOnClickListener(view1 -> {
                Navigation.findNavController(view1).navigate(R.id.action_homeFragment_to_unusualExpensesFragment);
            });
        } else {
            binding.unusualExpensesCv.setVisibility(View.INVISIBLE);
        }

        recyclerView = view.findViewById(R.id.transactions_recList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionsList = new ArrayList<>();

        AccessTokenRequest request = new AccessTokenRequest(user.getAccessToken());
        Model.instance().getUserCreditCard(request,
                (success) -> {
                    CreditCard card = Model.instance().getCreditCard();
                    String last4Numbers = card.getCardNum().substring(card.getCardNum().length() - 4);
                    creditCardCode.setText("XXXX - XXXX - XXXX - " + last4Numbers);
                    creditCardHolder.setText(card.getHolderName());
                    creditCardMonth.setText(card.getMonth());
                    creditCardYear.setText(card.getYear());
                },
                (error) -> {
                    Log.d("error" ,"getting card");
                }
        );


        transactionsList.add(new Transactions(R.drawable.shopping_cart, "Super market", "08/04/2023", "159"));
        transactionsList.add(new Transactions(R.drawable.shopping_bag, "KSP", "08/04/2023", "299"));
        transactionsList.add(new Transactions(R.drawable.shopping_bag, "Shopping", "03/04/2023", "10000"));
        transactionsList.add(new Transactions(R.drawable.shopping_cart, "KSP", "07/04/2023", "3500"));
        transactionsList.add(new Transactions(R.drawable.parents_transfer, "Mom transfer", "01/04/2023", "200"));
        transactionsList.add(new Transactions(R.drawable.shopping_cart, "AM PM", "07/04/2023", "79"));
        transactionsList.add(new Transactions(R.drawable.shopping_cart, "Super-Pharm", "07/04/2023", "120"));


        homeAdapter = new HomeAdapter(getContext(), transactionsList);
        recyclerView.setAdapter(homeAdapter);
        homeAdapter.notifyDataSetChanged();


        // z-score algorithm

        // Calculate the mean and standard deviation of the expenses
        double mean = 0.0;
        double stdDev = 0.0;
        for (Transactions transaction : transactionsList) {
            double expense = Double.parseDouble(transaction.getSum().replaceAll("[^\\d.-]", ""));
            mean += expense;
        }
        mean /= transactionsList.size();
        Log.d("result", String.valueOf(mean));

        for (Transactions transaction : transactionsList) {
            double expense = Double.parseDouble(transaction.getSum().replaceAll("[^\\d.-]", ""));
            stdDev += Math.pow(expense - mean, 2);
        }
        stdDev = Math.sqrt(stdDev / (transactionsList.size() - 1));
        Log.d("result", String.valueOf(stdDev));


        // Set the Z-score threshold
        double zScoreThreshold = 1.0;

        // Calculate the Z-score for each expense and mark irregular expenses
        for (Transactions transaction : transactionsList) {
            double expense = Double.parseDouble(transaction.getSum().replaceAll("[^\\d.-]", ""));
            double zScore = (expense - mean) / stdDev;
            transaction.setZScore(zScore);
            Log.d("result", String.valueOf(transaction.getZScore()));

            if (Math.abs(zScore) > zScoreThreshold) {
                // This expense is irregular
                     // need to pop notification
                Log.d("Irregular Expense", transaction.getSum());
            } else {
                // This expense is not irregular
                Log.d("Normal Expense", transaction.getSum());
            }
        }

        return view;
    }
}