package com.example.unccrudapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unccrudapp.model.Loja;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LojaActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    private Button botao01;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<Loja> loja = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private LojaAdapter lojaAdapter;
    private String url = "http://10.0.2.2:3000/lojas/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loja_acticity);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeDown);
        recyclerView = (RecyclerView) findViewById(R.id.loja);

        dialog = new Dialog(this);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loja.clear();
                getData();

                botao01 = (Button) findViewById(R.id.botao01);

                botao01.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        botao01Activity();
                    }
                });
            }
        });
    }

    private void botao01Activity() {

        startActivity(new Intent(LojaActivity.this, MenuActivity.class));
    }

    private void getData() {
        refreshLayout.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Loja u = new Loja();
                        u.setId(jsonObject.getString("_id"));
                        u.setNome(jsonObject.getString("nome"));
                        u.setSite(jsonObject.getString("site"));
                        u.setCidade(jsonObject.getString("cidade"));
                        u.setTipo(jsonObject.getString("tipo"));
                        loja.add(u);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(loja);
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue = Volley.newRequestQueue(LojaActivity.this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Loja> loja) {
        lojaAdapter = new LojaAdapter(this, loja);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(lojaAdapter);
    }

    public void addLoja(View v) {
        TextView txtLoja, txtClose;
        EditText edtNome, edtSite, edtCidade, edtTipo, edtEstado;
        Button btnSave;

        dialog.setContentView(R.layout.activity_modloja);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtLoja = (TextView) dialog.findViewById(R.id.txtLoja);

        txtLoja.setText("Nova Loja");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtNome = (EditText) dialog.findViewById(R.id.edtNome);
        edtSite = (EditText) dialog.findViewById(R.id.edtSite);
        edtCidade = (EditText) dialog.findViewById(R.id.edtCidade);
        edtTipo = (EditText) dialog.findViewById(R.id.edtTipo);
        edtEstado = (EditText) dialog.findViewById(R.id.edtEstado);

        btnSave = (Button) dialog.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("nome", edtNome.getText());
                    object.put("site", edtSite.getText());
                    object.put("cidade", edtCidade.getText());
                    object.put("tipo", edtTipo.getText());
                    object.put("estado", edtEstado.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submit(object);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submit(JSONObject object) {
        String url = "http://10.0.2.2:3000/lojas/create";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        loja.clear();
                        getData();
                    }
                });
                Toast.makeText(getApplicationContext(), "Dados gravados com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Erro ao gravar dados!", Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @Override
    public void onRefresh() {
        loja.clear();
        getData();
    }
}