package com.example.unccrudapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.unccrudapp.model.Loja;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class
LojaAdapter extends RecyclerView.Adapter<LojaAdapter.MyViewHolder> {
    private SwipeRefreshLayout refreshLayout;
    private RequestQueue requestQueue;
    private JsonArrayRequest arrayRequest;
    private Context context;
    private ArrayList<Loja> loja;
    private String url = "";
    private LojaAdapter lojaAdapter;
    private RecyclerView recyclerView;

    public LojaAdapter(Context context, ArrayList<Loja> loja) {
        this.context = context;
        this.loja = loja;
    }

    @NonNull
    @Override
    public LojaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.loja_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LojaAdapter.MyViewHolder holder, int position) {
        holder.txtSite.setText(loja.get(position).getNome());
        holder.txtNumber.setText(String.valueOf(position + 1));
        holder.edtLoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = loja.get(position).getId();
                String nome = loja.get(position).getNome();
                String site = loja.get(position).getSite();
                String cidade = loja.get(position).getCidade();
                String tipo = loja.get(position).getTipo();
                String estado = loja.get(position).getEstado();
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", id);
                    object.put("nome", nome);
                    object.put("site", site);
                    object.put("cidade", cidade);
                    object.put("tipo", tipo);
                    object.put("estado", estado);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  Log.d("usuario: ", object.toString());
                editLoja(id, object);
            }
        });
        holder.deleteLoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = loja.get(position).getId();
                deleteLoja(id);
            }
        });
    }

    private void deleteLoja(final String id) {
        TextView txtLoja, txtClose;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.loja_delete);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtLoja = (TextView) dialog.findViewById(R.id.txtLoja);

        txtLoja.setText("Excluir Loja");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave = (Button) dialog.findViewById(R.id.btnDelete);
        String lojaId = id;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(dialog, lojaId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void delete(Dialog dialog, String lojaId) {
        String url = "http://10.0.2.2:3000/lojas/delete/" + lojaId;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Toast.makeText(context, "Dados excluídos com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void editLoja(final String id, JSONObject object) {
        TextView txtLoja, txtClose;
        EditText edtNome, edtSite, edtCidade, edtTipo, edtEstado;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_modloja);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtLoja = (TextView) dialog.findViewById(R.id.txtLoja);

        txtLoja.setText("Alterar Loja");

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
        String lojaId = null;
        try {
            lojaId = object.getString("_id");
            edtNome.setText(object.getString("nome"));
            edtSite.setText(object.getString("site"));
            edtCidade.setText(object.getString("cidade"));
            edtTipo.setText(object.getString("tipo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalLojaId = lojaId;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", finalLojaId);
                    object.put("nome", edtNome.getText());
                    object.put("site", edtSite.getText());
                    object.put("cidade", edtCidade.getText());
                    object.put("tipo", edtTipo.getText());
                    object.put("estado", edtEstado.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submit(object, dialog, finalLojaId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submit(final JSONObject object, final Dialog dialog, String id) {
        String url = "http://10.0.2.2:3000/lojas/update/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                /*
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        loja.clear();
                        getData();
                    }
                });
                 */
                Toast.makeText(context, "Dados alterados com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Erro ao alterar dados!", Toast.LENGTH_LONG).show();
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
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
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        //requestQueue = Volley.newRequestQueue(LojaActivity.this);
        //requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Loja> loja) {
        //lojaAdapter = new LojaAdapter(this, loja);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(lojaAdapter);
    }

    @Override
    public int getItemCount() {
        return loja.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtSite, txtNumber;
        private ImageView edtLoja, deleteLoja;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumber = (TextView) itemView.findViewById(R.id.idNumber);
            txtSite = (TextView) itemView.findViewById(R.id.nameLoja);
            edtLoja = (ImageView) itemView.findViewById(R.id.editLoja);
            deleteLoja = (ImageView) itemView.findViewById(R.id.deleteLoja);
        }
    }
}
