package com.generation.blogpessoal.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.JwtHelper;
import com.generation.blogpessoal.util.TestBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final String Base_URL = "/usuarios";
    private static final String Usuario = "root@root.com";
    private static final String Senha = "rootroot";

    @BeforeAll
    void inicio() {
        usuarioRepository.deleteAll();
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Root", Usuario, Senha));
    }

    @Test
    @DisplayName("01 - Deve Cadastrar um novo usuário com sucesso")
    void deveCadastrarUsuario() {
        // Given
        Usuario usuario = TestBuilder.criarUsuario(null, "Gabrielle Guimarães", "gabrielle@email.com.br", "gabi1234");

        // When

        // Corpo da Requisição
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuario);

        // Enviar a Requisição
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(Base_URL + "/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

        // Then

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());

    }

    @Test
    @DisplayName("02 - Não Deve Cadastrar um novo usuário duplicado")
    void naoDeveCadastrarUsuarioDuplicado() {
        // Given
        Usuario usuario = TestBuilder.criarUsuario(null, "Luiza Guimarães", "luiza@email.com.br", "luiza1234");
        usuarioService.cadastrarUsuario(usuario);

        // When

        // Corpo da Requisição
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuario);

        // Enviar a Requisição
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(Base_URL + "/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

        // Then

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        assertNull(resposta.getBody());

    }

    @Test
    @DisplayName("03 - Deve Listar todos os usuários")
    void deveListarTodosUsuarios() {
        // Given
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Kaue Dota", "kaue@email.com.br", "kaue1234"));
        usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Edson Nascimento", "edson@email.com.br", "edson1234"));

        // When

        // Obter o Token
        String token = JwtHelper.obterToken(testRestTemplate, Usuario, Senha);

        // Cabeçalho da Requisição
        HttpEntity<Void> cabecalhoRequisicao = JwtHelper.criarRequisicaoComToken(token);

        // Enviar a Requisição
        ResponseEntity<Usuario[]> resposta = testRestTemplate.exchange(Base_URL + "/all", HttpMethod.GET, cabecalhoRequisicao, Usuario[].class);

        // Then

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());

    }

    @Test
    @DisplayName("04 - Deve Atualizar os dados do usuário com sucesso")
    void deveAtualizarUsuario() {
        // Given

        // Objeto para fazer o cadastro
        Usuario usuario = TestBuilder.criarUsuario(null, "Daniel", "daniel@email.com.br", "daniel1234");

        // Fiz o cadastro e guardei os dados objeto
        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);

        // Preparar o objeto com a atualização
        Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Daniel Araujo", "daniel_araujo@email.com.br", "abcd1234");

        // When

        // Obter o Token
        String token = JwtHelper.obterToken(testRestTemplate, Usuario, Senha);

        // Cabeçalho da Requisição
        HttpEntity<Usuario> cabecalhoRequisicao = JwtHelper.criarRequisicaoComToken(usuarioUpdate,token);

        // Enviar a Requisição
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(Base_URL + "/atualizar", HttpMethod.PUT, cabecalhoRequisicao, Usuario.class);

        // Then

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());

    }

    @Test
    @DisplayName("05 - Deve Listar por ID")
    void deveListarPorID() {

        // Given
        Usuario usuario = TestBuilder.criarUsuario(null, "Maryane Praxedes", "maryane@email.com.br", "maryane1234");

        Usuario usuarioCadastrado = usuarioService.cadastrarUsuario(usuario).get();

        // When

        // Obter o Token
        String token = JwtHelper.obterToken(testRestTemplate, Usuario, Senha);

        // Cabeçalho da Requisição
        HttpEntity<Void> cabecalhoRequisicao = JwtHelper.criarRequisicaoComToken(token);

        // Enviar a Requisição
        ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
                Base_URL + "/" + usuarioCadastrado.getId(),
                HttpMethod.GET,
                cabecalhoRequisicao,
                Usuario.class
        );

        // Then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(usuarioCadastrado.getId(), resposta.getBody().getId());
    }
}