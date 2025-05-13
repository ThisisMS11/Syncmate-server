package com.SyncMate.SyncMate;

import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.repository.EmailRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

@SpringBootTest
public class Temp {

    @Autowired
    private EmailRecordRepository emailRecordRepository;

   @Test
   public void testFindAllEmailRecords() {
       List<EmailRecord> records = emailRecordRepository.findAllWithContact();
       assertThat(records).isNotNull();

       System.out.println(records.get(0).getContact().getEmail());
   }
}
