package commonmodules;


class DateTimeDifferance {
    long seconds;
    long minutes;
    long hours;
    long days;
    int year;
    int month;

    public DateTimeDifferance(long days, long hours, long minutes, int month, long seconds, int year) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.month = month;
        this.seconds = seconds;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }
}
