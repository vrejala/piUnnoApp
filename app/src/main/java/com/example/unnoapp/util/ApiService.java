package com.example.unnoapp.util;


import com.example.unnoapp.modelo.Profissional;
import com.example.unnoapp.modelo.Usuario;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("usuarios")
    Call<List<Usuario>> getUsuarios();

    @POST("usuarios")
    Call<Usuario> criarUsuario(@Body Usuario usuario);

    // Profissionais
    @GET("profissionais")
    Call<List<Profissional>> getProfissionais();

    @POST("profissionais")
    Call<Profissional> criarProfissional(@Body Profissional p);

    @GET("usuarios/{id}")
    Call<Usuario> getUsuario(@Path("id") int id);
}