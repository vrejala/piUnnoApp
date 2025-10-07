package com.example.unnoapp.util;


import com.example.unnoapp.modelo.ChangePasswordRequest;
import com.example.unnoapp.modelo.Cliente;
import com.example.unnoapp.modelo.EmailRequest;
import com.example.unnoapp.modelo.GoogleLoginRequest;
import com.example.unnoapp.modelo.HealthResponse;
import com.example.unnoapp.modelo.Profissional;
import com.example.unnoapp.modelo.ResetPasswordRequest;
import com.example.unnoapp.modelo.Usuario;
import com.example.unnoapp.modelo.UsuarioLogin;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // Endpoint de health check
    @GET("health")
    Call<HealthResponse> healthCheck();

    @POST("usuarios/login")
    Call<Usuario> login(@Body UsuarioLogin credenciais);

    @POST("usuarios/login")
    Call<Usuario> login(@Body Usuario usuario);

    // Enviar email para redefinição de senha
    @POST("recuperar-senha/solicitar/")
    Call<Void> enviarEmailReset(@Body EmailRequest emailRequest);

    // Resetar senha com token
    @POST("recuperar-senha/confirmar/")
    Call<Void> confirmarReset(@Body ResetPasswordRequest request);


    // Alterar senha (com email, senha atual e nova senha)
    @POST("usuarios/change-password")
    Call<Void> trocarSenha(@Body ChangePasswordRequest changePasswordRequest);

    @GET("/usuarios")
    Call<List<Usuario>> getUsuarios();

    @GET("/usuarios/{id}")
    Call<Usuario> getUsuario(@Path("id") int id);

    @POST("usuarios/")
    Call<Usuario> criarUsuario(@Body Usuario usuario);

    @GET("usuarios/nome/{nome}")
    Call<List<Usuario>> buscarNome(@Path("nome") String nome);

    @PUT("/usuarios/{id}")
    Call<Usuario> atualizarUsuario(@Path("id") int id, @Body Usuario usuario);

    @DELETE("usuarios/{id}")
    Call<Void> deletarUsuario(@Path("id") int id);

    @POST("usuarios")
    @Headers("Content-Type: application/json")
    Call<List<Usuario>> addUsuario(@Body Usuario usuario);

    // PATCH com filtro id=eq.{id} — note encoded=true para evitar encoding do '=' e '.'
    @PATCH("usuarios?id=eq.{id}")
    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    Call<List<Usuario>> updateUsuario(@Path(value = "id", encoded = true) int id, @Body Usuario usuario);

    @DELETE("usuarios?id=eq.{id}")
    @Headers("Prefer: return=representation")
    Call<List<Usuario>> deleteUsuario(@Path(value = "id", encoded = true) int id);

    @POST("usuarios/login/google")
    Call<Usuario> loginGoogle(@Body GoogleLoginRequest request);

    // Clientes
    @GET("clientes")
    Call<List<Cliente>> getClientes();

    @GET("clientes/{id}")
    Call<Cliente> getCliente(@Path("id") int id);

    @POST("clientes/")
    Call<Cliente> criarCliente(@Body Cliente cliente);

    @Multipart
    @POST("clientes/com-foto")
    Call<Cliente> criarClienteComFoto(
            @Part("nome") RequestBody nome,
            @Part("telefone") RequestBody telefone,
            @Part("cpf") RequestBody cpf,
            @Part("email") RequestBody email,
            @Part("endereco") RequestBody endereco,
            @Part("numero") RequestBody numero,
            @Part("cep") RequestBody cep,
            @Part("administrador") RequestBody administrador,
            @Part MultipartBody.Part foto
    );

    @PUT("clientes/{id}")
    Call<Cliente> atualizarCliente(@Path("id") int id, @Body Cliente cliente);

    @DELETE("clientes/{id}")
    Call<Void> deletarCliente(@Path("id") int id);

    @GET("clientes/nome/{nome}")
    Call<List<Cliente>> buscarClientePorNome(@Path("nome") String nome);

    @GET("clientes/usuario/{usuarioId}")
    Call<Cliente> buscarClientePorUsuario(@Path("usuarioId") int usuarioId);


    // Profissionais
    @GET("/profissionais")
    Call<List<Profissional>> getProfissional();

    @GET("/profissionais/{id}")
    Call<Profissional> getProfissional(@Path("id") int id);

    @POST("profissionais/")
    Call<Profissional> criarProfissional(@Body Profissional profissionais);

    @GET("profissionais/")
    Call<List<Profissional>> getProfissionais();

    @Multipart
    @POST("profissionais/com-foto")
    Call<Profissional> criarProfissionalComFoto(
            @Part("nome") RequestBody nome,
            @Part("cadastroprofissional") RequestBody cadastroprofissional,
            @Part("email") RequestBody email,
            @Part("telefone") RequestBody telefone,
            @Part("valor") RequestBody valor,
            @Part("sobre") RequestBody sobre,
            @Part("especialidade") RequestBody especialidade,
            @Part("abordagem") RequestBody abordagem,
            @Part("tipopagamento") RequestBody tipopagamento,
            @Part("ativo") RequestBody ativo,
            @Part MultipartBody.Part foto
    );

    @PUT("/profissionais/{id}")
    Call<Profissional> atualizarProfissional(@Path("id") int id, @Body Profissional profissionais);

    @GET("profissionais/nome/{nome}")
    Call<List<Profissional>> buscarPorNome(@Path("nome") String nome);

    @DELETE("/profissionais/{id}")
    Call<Void> deletarProfissional(@Path("id") int id);

    @GET("profissionais/especialidade/{especialidade}")
    Call<List<Profissional>> getProfissionaisPorEspecialidade(@Path("especialidade") String especialidade);

    Call<Cliente> criarClienteComFoto(RequestBody nomePart, RequestBody telefonePart, RequestBody cpfPart, RequestBody emailPart, RequestBody enderecoPart, RequestBody numeroPart, RequestBody cepPart, MultipartBody.Part fotoPart);

    Call<Cliente> atualizarClienteComFoto(int clienteIdAtual, RequestBody nomePart, RequestBody telefonePart, RequestBody cpfPart, RequestBody emailPart, RequestBody enderecoPart, RequestBody numeroPart, RequestBody cepPart, MultipartBody.Part fotoPart);
}