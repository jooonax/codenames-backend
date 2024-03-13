package at.kaindorf.codenames.pojos;

import lombok.Data;

/**
 * @author : Jonas
 * @created : 13/03/2024, Wednesday
 **/
@Data
public class Message {
    private String from;
    private String to;
    private String content;
}
