package com.SyncMate.SyncMate;

import com.SyncMate.SyncMate.entity.APIkey;
import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.repository.APIKeyRepository;
import com.SyncMate.SyncMate.repository.EmailRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

@SpringBootTest
public class Temp {

    @Autowired
    private APIKeyRepository apiKeyRepository;

   @Test
   public void testFindAllEmailRecords() {
       String apikeyhash = "0e8f426f6ed12d584c402e004a46fec2ea3c6712f2d18a4d00c0bd8aa38516a1";
       APIkey apIkey = apiKeyRepository.findUserWithApiKey(apikeyhash);
       User user1 = apIkey.getUser();
       assertThat(user1).isNotNull();

       User user2 = apiKeyRepository.findByApiKeyHash(apikeyhash).getUser();
       assertThat(user2).isNotNull();
   }
}
