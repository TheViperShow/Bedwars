package me.thevipershow.aussiebedwars.bedwars.objects;

public enum BedwarsTeam {
    CYAN(9, '3'),
    PINK(6, 'd'),
    YELLOW(4, 'e'),
    GREEN(5, '2'),
    RED(14, '4'),
    BLUE(11, '1'),
    WHITE(0, 'f'),
    GREY(8, '7');

    private final int woolColor;

    private final char colorCode;

    BedwarsTeam(int woolColor, char colorCode) {
        this.woolColor = woolColor;
        this.colorCode = colorCode;
    }

    public int getWoolColor() {
        return woolColor;
    }

    public char getColorCode() {
        return colorCode;
    }
}
