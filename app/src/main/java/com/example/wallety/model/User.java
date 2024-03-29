package com.example.wallety.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    @SerializedName("id")
    private String id = "";

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("balance")
    private int balance;

    @SerializedName("creditCard")
    private CreditCard creditCard;

    @SerializedName("transactions")
    private List<Transaction> transactions;

    @SerializedName("savings")
    private List<Saving> savings;

    @SerializedName("tasks")
    private List<Task> tasks;

    @SerializedName("children")
    private List<User> children;

    @SerializedName("registrationToken")
    public String registrationToken;

    @SerializedName("lastUpdated")
    public Long lastUpdated;

    @SerializedName("accessToken")
    private String accessToken = "";

    public User(String name, String phone, String email, String password, int balance) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }

    public User(String id, String name, String phone, String email, String password, int balance) {
        this(name, email, password, phone, balance);
        this.id = id;
    }

    public static final String COLLECTION = "users";
    static final String ID = "id";
    static final String USER = "name";
    static final String PHONE = "phone";
    static final String EMAIL = "email";
    static final String PASSWORD = "password";
    static final String LAST_UPDATED = "lastUpdated";
    static final String BALANCE = "balance";


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getBalance() {
        return balance;
    }

    public List<User> getChildren() {
        return children;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Saving> getSavings() {
        return savings;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public String getAccessToken() {
        if (accessToken == null) {
            accessToken = "";
        }

        return accessToken;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void setChildren(List<User> children) {
        this.children = children;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isParent() {
        return children != null;
    }

    public void addSaving(Saving saving) {
        if (savings == null) {
            savings = new ArrayList<>();
        }
        savings.add(saving);
    }

    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
    }

    public void increaseBalance(int amount) {
        balance += amount;
    }

    public void makeTransaction(Transaction transaction) {
        int transactionAmount = transaction.getAmount();
        balance -= transactionAmount;
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(transaction);

        Saving transactionSaving = transaction.getSaving();
        if (transactionSaving != null) {
            transactionSaving.increaseCurrentAmount(transactionAmount);
        }

        User childReceiver = transaction.getChildReceiver();
        if (childReceiver != null) {
            childReceiver.increaseBalance(transactionAmount);
        }
    }

    //
    public static User fromJson(Map<String, Object> json) {
        String id = (String) json.get(ID);
        String name = (String) json.get(USER);
        String phone = (String) json.get(PHONE);
        String email = (String) json.get(EMAIL);
        String password = (String) json.get(PASSWORD);
        int balance = Integer.parseInt((String) json.get(BALANCE));

        User user = new User(id, name, phone, email, password, balance);
        try {
            Timestamp time = (Timestamp) json.get(LAST_UPDATED);
            user.setLastUpdated(time.getSeconds());
        } catch (Exception e) {

        }

        return user;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put(ID, getId());
        json.put(USER, getName());
        json.put(PHONE, getPhone());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        json.put(BALANCE, getBalance());

        return json;
    }
}
