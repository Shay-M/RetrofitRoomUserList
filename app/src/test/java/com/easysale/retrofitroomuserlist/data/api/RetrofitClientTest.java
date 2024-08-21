package com.easysale.retrofitroomuserlist.data.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class RetrofitClientTest {

    @Test
    public void testGetApiService() {
        ApiService apiService = RetrofitClient.getApiService();
        assertNotNull(apiService);
    }

}