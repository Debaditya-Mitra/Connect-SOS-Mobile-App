package com.chatdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class UsersListActivity extends AppCompatActivity implements OnItemClickInterface {
    private final String baseUrl = "https://androidchatapp-168d7.firebaseio.com/";
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    ArrayList<UserVo> userList;
    RecyclerView recyclerUserList;
    String userId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersList = (ListView) findViewById(R.id.usersList);
        noUsersText = (TextView) findViewById(R.id.noUsersText);
        recyclerUserList = findViewById(R.id.recyclerUserList);
        userList = new ArrayList<>();
        userId = SharedPreferenceUtil.getString("UserId", "");

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        String url = baseUrl + "Users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = al.get(position);
                startActivity(new Intent(getApplicationContext(), chat.class));
            }
        });
    }



    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            //     if(obj!=null){
            Iterator keys = obj.keys();
            while (keys.hasNext()) {
                String key = String.valueOf(keys.next());
                JSONObject childobj = obj.getJSONObject(key);
                if (childobj != null) {
                    if (!key.equalsIgnoreCase(userId)) {
                        UserVo userVo = new UserVo();
                        userVo.setUserEmail(childobj.getString("email"));
                        userVo.setUserId(key);
                        userVo.setUserName(childobj.getString("username"));
                        //al.add(childobj.getString(""));
                        userList.add(userVo);
                        totalUsers++;
                    }
                }
            }
            //     }



        /*    Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                if(!key.equals(UserDetails.username)) {
                    al.add(key);
                }

                totalUsers++;
            }
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalUsers < 1) {
            noUsersText.setVisibility(View.VISIBLE);
            recyclerUserList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            recyclerUserList.setVisibility(View.VISIBLE);
            setAdapter();
            //usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userList));
        }

        pd.dismiss();
    }

    private void setAdapter() {
        UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userList, this);
        recyclerUserList.setLayoutManager(new LinearLayoutManager(this));
        recyclerUserList.setAdapter(userListAdapter);
    }

    @Override
    public void onItemClick(int position, UserVo userVo) {
        UserDetails.chatWith = userVo.userId;
        startActivity(new Intent(this, chat.class));
    }

    @Override
    public void onItemClick(String imageUrl) {

    }
}
package com.chatdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class UsersListActivity extends AppCompatActivity implements OnItemClickInterface {
    private final String baseUrl = "https://androidchatapp-168d7.firebaseio.com/";
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    ArrayList<UserVo> userList;
    RecyclerView recyclerUserList;
    String userId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersList = (ListView) findViewById(R.id.usersList);
        noUsersText = (TextView) findViewById(R.id.noUsersText);
        recyclerUserList = findViewById(R.id.recyclerUserList);
        userList = new ArrayList<>();
        userId = SharedPreferenceUtil.getString("UserId", "");

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        String url = baseUrl + "Users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = al.get(position);
                startActivity(new Intent(getApplicationContext(), chat.class));
            }
        });
    }



    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            //     if(obj!=null){
            Iterator keys = obj.keys();
            while (keys.hasNext()) {
                String key = String.valueOf(keys.next());
                JSONObject childobj = obj.getJSONObject(key);
                if (childobj != null) {
                    if (!key.equalsIgnoreCase(userId)) {
                        UserVo userVo = new UserVo();
                        userVo.setUserEmail(childobj.getString("email"));
                        userVo.setUserId(key);
                        userVo.setUserName(childobj.getString("username"));
                        //al.add(childobj.getString(""));
                        userList.add(userVo);
                        totalUsers++;
                    }
                }
            }
            //     }



        /*    Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                if(!key.equals(UserDetails.username)) {
                    al.add(key);
                }

                totalUsers++;
            }
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalUsers < 1) {
            noUsersText.setVisibility(View.VISIBLE);
            recyclerUserList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            recyclerUserList.setVisibility(View.VISIBLE);
            setAdapter();
            //usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userList));
        }

        pd.dismiss();
    }

    private void setAdapter() {
        UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userList, this);
        recyclerUserList.setLayoutManager(new LinearLayoutManager(this));
        recyclerUserList.setAdapter(userListAdapter);
    }

    @Override
    public void onItemClick(int position, UserVo userVo) {
        UserDetails.chatWith = userVo.userId;
        startActivity(new Intent(this, chat.class));
    }

    @Override
    public void onItemClick(String imageUrl) {

    }
}
package com.chatdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class UsersListActivity extends AppCompatActivity implements OnItemClickInterface {
    private final String baseUrl = "https://androidchatapp-168d7.firebaseio.com/";
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    ArrayList<UserVo> userList;
    RecyclerView recyclerUserList;
    String userId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersList = (ListView) findViewById(R.id.usersList);
        noUsersText = (TextView) findViewById(R.id.noUsersText);
        recyclerUserList = findViewById(R.id.recyclerUserList);
        userList = new ArrayList<>();
        userId = SharedPreferenceUtil.getString("UserId", "");

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        String url = baseUrl + "Users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = al.get(position);
                startActivity(new Intent(getApplicationContext(), chat.class));
            }
        });
    }



    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            //     if(obj!=null){
            Iterator keys = obj.keys();
            while (keys.hasNext()) {
                String key = String.valueOf(keys.next());
                JSONObject childobj = obj.getJSONObject(key);
                if (childobj != null) {
                    if (!key.equalsIgnoreCase(userId)) {
                        UserVo userVo = new UserVo();
                        userVo.setUserEmail(childobj.getString("email"));
                        userVo.setUserId(key);
                        userVo.setUserName(childobj.getString("username"));
                        //al.add(childobj.getString(""));
                        userList.add(userVo);
                        totalUsers++;
                    }
                }
            }
            //     }



        /*    Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                if(!key.equals(UserDetails.username)) {
                    al.add(key);
                }

                totalUsers++;
            }
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalUsers < 1) {
            noUsersText.setVisibility(View.VISIBLE);
            recyclerUserList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            recyclerUserList.setVisibility(View.VISIBLE);
            setAdapter();
            //usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userList));
        }

        pd.dismiss();
    }

    private void setAdapter() {
        UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userList, this);
        recyclerUserList.setLayoutManager(new LinearLayoutManager(this));
        recyclerUserList.setAdapter(userListAdapter);
    }

    @Override
    public void onItemClick(int position, UserVo userVo) {
        UserDetails.chatWith = userVo.userId;
        startActivity(new Intent(this, chat.class));
    }

    @Override
    public void onItemClick(String imageUrl) {

    }
}
