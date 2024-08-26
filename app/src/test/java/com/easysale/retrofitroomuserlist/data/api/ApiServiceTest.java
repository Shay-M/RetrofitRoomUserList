//package com.easysale.retrofitroomuserlist.data.api;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//import com.easysale.retrofitroomuserlist.data.model.UserResponse;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import retrofit2.Call;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class ApiServiceTest {
//
//    private MockWebServer mockWebServer;
//    private ApiService apiService;
//
//    @Before
//    public void setUp() {
//        mockWebServer = new MockWebServer();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(mockWebServer.url("/"))  // Mock server URL
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        apiService = retrofit.create(ApiService.class);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        mockWebServer.shutdown();
//    }
//
//    @Test
//    public void testGetUsers() throws Exception {
//        // Prepare mock response
//        String mockResponseBody = "{ \"data\": [{ \"id\": 1, \"first_name\": \"John\", \"last_name\": \"Doe\" }] }";
//        mockWebServer.enqueue(new MockResponse()
//                .setBody(mockResponseBody)
//                .setResponseCode(200));
//
//        // Call the API
//        Call<UserResponse> call = apiService.getUsers(1);
//        retrofit2.Response<UserResponse> response = call.execute();
//
//        // Verify the response
//        assertEquals(200, response.code());
//        UserResponse userResponse = response.body();
//        assertNotNull(userResponse);
//        assertEquals("John", userResponse.getData().get(0).getFirstName());
//        assertEquals("Doe", userResponse.getData().get(0).getLastName());
//    }
//}
