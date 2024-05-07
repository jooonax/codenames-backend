package at.kaindorf.codenames.pojos;

import lombok.Data;

import java.util.List;

/**
 * Project: codenames-backend
 * Created by: raph
 * Date: 2024-05-07
 * Time: 14:03:12
 */

@Data
public class User {

    private String userName;
    private String roomCode;
    private List<String> receiverNames;

}
