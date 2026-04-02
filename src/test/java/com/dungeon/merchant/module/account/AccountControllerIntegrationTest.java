package com.dungeon.merchant.module.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dungeon.merchant.module.account.entity.Account;
import com.dungeon.merchant.module.account.repository.AccountMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(statements = {
    "DELETE FROM account",
    "INSERT INTO account (id, username, password, created_at, updated_at) VALUES (1, 'merchant', 'pw', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
    "INSERT INTO account (id, username, password, created_at, updated_at) VALUES (2, 'blacksmith', 'pw', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
})
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountMapper accountMapper;

    @Test
    void getAccountReturnsCurrentAccount() throws Exception {
        mockMvc.perform(get("/api/account").requestAttr("accountId", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("success"))
            .andExpect(jsonPath("$.data.id").value(1L))
            .andExpect(jsonPath("$.data.username").value("merchant"));
    }

    @Test
    void updateAccountRenamesCurrentAccount() throws Exception {
        mockMvc.perform(put("/api/account")
                .requestAttr("accountId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"guild-master"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(content().string(containsString("\"data\":null")));

        Account account = accountMapper.selectById(1L);
        assertThat(account.getUsername()).isEqualTo("guild-master");
    }

    @Test
    void updateAccountRejectsDuplicateUsernameWithConflictStatus() throws Exception {
        mockMvc.perform(put("/api/account")
                .requestAttr("accountId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"blacksmith"}
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(409))
            .andExpect(jsonPath("$.message").value("用户名已存在"));
    }
}
