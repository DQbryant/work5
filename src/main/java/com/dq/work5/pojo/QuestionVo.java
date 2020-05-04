package com.dq.work5.pojo;

/**
 *
 */
public class QuestionVo {
    private String username;
    private String email;
    private String ausername;
    private String aemail;
    private String content;
    private String answer;

    @Override
    public String toString() {
        return "QuestionVo{" +
                " username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", ausername='" + ausername + '\'' +
                ", aemail='" + aemail + '\'' +
                ", content='" + content + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAusername() {
        return ausername;
    }

    public void setAusername(String ausername) {
        this.ausername = ausername;
    }

    public String getAemail() {
        return aemail;
    }

    public void setAemail(String aemail) {
        this.aemail = aemail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
