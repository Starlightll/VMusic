package models;

public class LyricLine {
    public float time;

    public String text;

    public LyricLine(float time, String text) {
        this.time = time;
        this.text = text;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
