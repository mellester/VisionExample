package com.example.demo;

import java.util.Map;

public record StoreRequest(String kenteken, Map<String,String> gegevens) {
}
