package SimpleYesChat.YesChat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo  extends JpaRepository<ChatMessage, Long> {
     ChatMessage findById(long id);
     List<ChatMessage> findByIsReceived(boolean check);
}
