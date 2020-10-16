package me.thevipershow.bedwars.bedwars.objects;

import org.bukkit.Color;

public enum BedwarsTeam {
    CYAN((short) 9, '3', Color.fromRGB(53, 165, 165), (short) 9),
    PINK((short) 6, 'd', Color.fromRGB(255, 0, 235), (short) 6),
    YELLOW((short) 4, 'e', Color.fromRGB(255, 255, 0), (short) 4),
    GREEN((short) 5, '2', Color.fromRGB(111, 255, 0), (short) 5),
    RED((short) 14, '4', Color.fromRGB(255, 0, 0), (short) 14),
    BLUE((short) 11, '1', Color.fromRGB(0, 25, 255), (short) 11),
    WHITE((short) 0, 'f', Color.fromRGB(255, 255, 255), (short) 0),
    GREY((short) 8, '7', Color.fromRGB(156, 156, 156), (short) 8);

    private final short woolColor;
    private final char colorCode;
    private final Color RGBColor;
    private final short glassColor;

    BedwarsTeam(short woolColor, char colorCode, Color RGBColor, short glassColor) {
        this.woolColor = woolColor;
        this.colorCode = colorCode;
        this.RGBColor = RGBColor;
        this.glassColor = glassColor;
    }

    public short getWoolColor() {
        return woolColor;
    }

    public char getColorCode() {
        return colorCode;
    }

    public Color getRGBColor() {
        return RGBColor;
    }

    public short getGlassColor() {
        return glassColor;
    }

}
