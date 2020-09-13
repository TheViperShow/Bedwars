package me.thevipershow.aussiebedwars.bedwars.objects;

import org.bukkit.Color;

public enum BedwarsTeam {
    CYAN((short) 9, '3', Color.fromRGB(53, 165, 165)),
    PINK((short) 6, 'd', Color.fromRGB(255, 0, 235)),
    YELLOW((short) 4, 'e', Color.fromRGB(255, 255, 0)),
    GREEN((short) 5, '2', Color.fromRGB(111, 255, 0)),
    RED((short) 14, '4', Color.fromRGB(255, 0, 0)),
    BLUE((short) 11, '1', Color.fromRGB(0, 25, 255)),
    WHITE((short) 0, 'f', Color.fromRGB(255, 255, 255)),
    GREY((short) 8, '7', Color.fromRGB(156, 156, 156));

    private final short woolColor;
    private final char colorCode;
    private final Color rgbColour;


    BedwarsTeam(short woolColor, char colorCode, Color rgbColour) {
        this.woolColor = woolColor;
        this.colorCode = colorCode;
        this.rgbColour = rgbColour;
    }

    public short getWoolColor() {
        return woolColor;
    }

    public char getColorCode() {
        return colorCode;
    }

    public Color getRgbColour() {
        return rgbColour;
    }

    public static BedwarsTeam fromWoolDurability(final short durability) {
        for (BedwarsTeam value : values()) {
            if (value.woolColor == durability) return value;
        }
        return null;
    }
}
