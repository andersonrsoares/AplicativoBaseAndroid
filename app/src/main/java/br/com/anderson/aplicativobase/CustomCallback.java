package br.com.anderson.aplicativobase;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by DevMaker on 7/11/16.
 */
class CustomCallback<T> implements Callback<T> {
    Context context;
    ProgressDialog dialog;
    OnResponse onResponse;

    public CustomCallback(Context context,OnResponse<T> onResponse){
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Buscando dados...");
        dialog.show();

        this.onResponse = onResponse;
    }

    @Override
    public void onResponse(Call<T> call, retrofit2.Response<T> response) {
        dialog.dismiss();
        if(response.isSuccessful())
            onResponse.onResponse(response.body());
        else{
            try {
                onResponse.onFailure(new Throwable(response.errorBody().string()));
            }catch (Exception ex){
                onResponse.onFailure(new Throwable("Ocorreu um erro"));
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, final Throwable t) {
        dialog.dismiss();
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //define o titulo
        builder.setTitle("Problema de conexao");
        //define a mensagem
        builder.setMessage("Gostaria de tentar novamente");
        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                onResponse.onRetry(t);
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                onResponse.onFailure(t);
            }
        });
        //cria o AlertDialog
        AlertDialog alerta = builder.create();
        //Exibe
        alerta.show();
    }

    public interface OnResponse<T>{
        public void onResponse(T response);
        public void onFailure(Throwable t);
        public void onRetry(Throwable t);
    }


}

