package com.ivy.forum.controller;

import com.ivy.forum.common.Result;
import com.ivy.forum.security.SecurityUser;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        SecurityUser principal = new SecurityUser(1L, "admin", "ADMIN");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void CTRL_SMOKE_001_Post_list() throws Exception {
        mockMvc.perform(get("/posts")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void CTRL_SMOKE_002_Post_hot() throws Exception {
        mockMvc.perform(get("/posts/hot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void CTRL_SMOKE_003_Post_detail() throws Exception {
        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void CTRL_SMOKE_004_Post_create() throws Exception {
        String json = """
                {"channelId":1,"title":"CTRL测试发帖","content":"冒烟测试内容"}
                """;
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void CTRL_SMOKE_005_Post_delete() throws Exception {
        String json = """
                {"channelId":1,"title":"待删除","content":"将被删除"}
                """;
        String resp = mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(resp, "$.data");
        mockMvc.perform(delete("/posts/" + id.longValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void CTRL_SMOKE_006_Post_detail_not_found() throws Exception {
        mockMvc.perform(get("/posts/99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(3001));
    }

    @Test
    void CTRL_SMOKE_007_Channel_list() throws Exception {
        mockMvc.perform(get("/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void CTRL_SMOKE_008_Channel_tree() throws Exception {
        mockMvc.perform(get("/channels/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void CTRL_SMOKE_009_Tag_list() throws Exception {
        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void CTRL_SMOKE_010_Tag_hot() throws Exception {
        mockMvc.perform(get("/tags/hot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void CTRL_SMOKE_011_Comment_list() throws Exception {
        mockMvc.perform(get("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void CTRL_SMOKE_012_Auth_register() throws Exception {
        String uname = "ctrltest_" + System.currentTimeMillis();
        String json = String.format("""
                {"username":"%s","password":"123456","nickname":"CTRL测试"}
                """, uname);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").isString());
    }

    @Test
    void CTRL_SMOKE_013_Auth_register_then_login() throws Exception {
        String uname = "logintest_" + System.currentTimeMillis();
        String regJson = String.format("""
                {"username":"%s","password":"test123","nickname":"登录测试"}
                """, uname);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(regJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        String loginJson = String.format("""
                {"username":"%s","password":"test123"}
                """, uname);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").isString());
    }

    @Test
    void CTRL_SMOKE_014_Auth_login_wrong_password() throws Exception {
        String json = """
                {"username":"admin","password":"wrong_password"}
                """;
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1003));
    }
}
