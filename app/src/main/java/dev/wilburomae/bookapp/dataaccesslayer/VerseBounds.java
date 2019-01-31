package dev.wilburomae.bookapp.dataaccesslayer;

public class VerseBounds {
    private int start, end;

    public VerseBounds(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start < end ? start : end;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return start < end ? end : start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean equals(VerseBounds toCompare) {
        return (toCompare.getStart() == this.getStart()) && (toCompare.getEnd() == this.getEnd());
    }

    @Override
    public String toString() {
        return "VerseBounds{" +
                "start=" + getStart() +
                ", end=" + getEnd() +
                '}';
    }
}