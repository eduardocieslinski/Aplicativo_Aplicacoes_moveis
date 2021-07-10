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
import com.example.unccrudapp.model.Produto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class
ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.MyViewHolder> {
    private SwipeRefreshLayout refreshLayout;
    private RequestQueue requestQueue;
    private JsonArrayRequest arrayRequest;
    private Context context;
    private ArrayList<Produto> produto;
    private String url = "";
    private ProdutoAdapter produtoAdapter;
    private RecyclerView recyclerView;

    public ProdutoAdapter(Context context, ArrayList<Produto> produto) {
        this.context = context;
        this.produto = produto;
    }

    @NonNull
    @Override
    public ProdutoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.produto_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoAdapter.MyViewHolder holder, int position) {
        holder.txtTipo.setText(produto.get(position).getNome());
        holder.txtNumber.setText(String.valueOf(position + 1));
        holder.edtProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = produto.get(position).getId();
                String nome = produto.get(position).getNome();
                String tipo = produto.get(position).getTipo();
                String preco = produto.get(position).getPreco();
                String marca = produto.get(position).getMarca();
                String foto = produto.get(position).getFoto();
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", id);
                    object.put("nome", nome);
                    object.put("tipo", tipo);
                    object.put("preco", preco);
                    object.put("marca", marca);
                    object.put("foto", foto);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  Log.d("usuario: ", object.toString());
                editProduto(id, object);
            }
        });
        holder.deleteProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = produto.get(position).getId();
                deleteProduto(id);
            }
        });
    }

    private void deleteProduto(final String id) {
        TextView txtProduto, txtClose;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.produto_delete);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtProduto = (TextView) dialog.findViewById(R.id.txtProduto);

        txtProduto.setText("Excluir Produto");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave = (Button) dialog.findViewById(R.id.btnDelete);
        String produtoId = id;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(dialog, produtoId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void delete(Dialog dialog, String produtoId) {
        String url = "http://10.0.2.2:3000/produtos/delete/" + produtoId;
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

    private void editProduto(final String id, JSONObject object) {
        TextView txtProduto, txtClose;
        EditText edtNome, edtTipo, edtPreco, edtMarca, edtFoto;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_modproduto);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtProduto = (TextView) dialog.findViewById(R.id.txtProduto);

        txtProduto.setText("Alterar Produto");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtNome = (EditText) dialog.findViewById(R.id.edtNome);
        edtTipo = (EditText) dialog.findViewById(R.id.edtTipo);
        edtPreco = (EditText) dialog.findViewById(R.id.edtPreco);
        edtMarca = (EditText) dialog.findViewById(R.id.edtMarca);
        edtFoto = (EditText) dialog.findViewById(R.id.edtFoto);

        btnSave = (Button) dialog.findViewById(R.id.btnSave);
        String produtoId = null;
        try {
            produtoId = object.getString("_id");
            edtNome.setText(object.getString("nome"));
            edtTipo.setText(object.getString("tipo"));
            edtPreco.setText(object.getString("preco"));
            edtMarca.setText(object.getString("marca"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalProdutoId = produtoId;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", finalProdutoId);
                    object.put("nome", edtNome.getText());
                    object.put("tipo", edtTipo.getText());
                    object.put("preco", edtPreco.getText());
                    object.put("marca", edtMarca.getText());
                    object.put("foto", edtFoto.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submit(object, dialog, finalProdutoId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submit(final JSONObject object, final Dialog dialog, String id) {
        String url = "http://10.0.2.2:3000/produtos/update/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                /*
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        produto.clear();
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
                        Produto u = new Produto();
                        u.setId(jsonObject.getString("_id"));
                        u.setNome(jsonObject.getString("nome"));
                        u.setTipo(jsonObject.getString("tipo"));
                        u.setPreco(jsonObject.getString("preco"));
                        u.setMarca(jsonObject.getString("marca"));
                        produto.add(u);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(produto);
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        //requestQueue = Volley.newRequestQueue(Produto.this);
        //requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Produto> produto) {
        //produto = new ProdutoAdapter(this, produto);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(produtoAdapter);
    }

    @Override
    public int getItemCount() {
        return produto.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTipo, txtNumber;
        private ImageView edtProduto, deleteProduto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumber = (TextView) itemView.findViewById(R.id.idNumber);
            txtTipo = (TextView) itemView.findViewById(R.id.nameProduto);
            edtProduto = (ImageView) itemView.findViewById(R.id.editProduto);
            deleteProduto = (ImageView) itemView.findViewById(R.id.deleteProduto);
        }
    }
}
