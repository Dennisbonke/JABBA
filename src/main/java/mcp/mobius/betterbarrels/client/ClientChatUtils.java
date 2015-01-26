package mcp.mobius.betterbarrels.client;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.*;

import java.util.ArrayList;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
@SideOnly(Side.CLIENT)
public class ClientChatUtils {

    private static final char sectionChar = '§';

    private static EnumChatFormatting getFormattingFromChar(char c)
    {
        for (EnumChatFormatting format : ) {
            if (format.func_96298_a() == c) {
                return format;
            }
        }
        return EnumChatFormatting.RESET;
    }

    private static void applyStyleFormat(ChatStyle style, EnumChatFormatting format)
    {
        switch (format.ordinal())
        {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                style.func_150238_a(format);
                break;
            case 17:
                style.func_150237_e(Boolean.valueOf(true));
                break;
            case 18:
                style.func_150227_a(Boolean.valueOf(true));
                break;
            case 19:
                style.func_150225_c(Boolean.valueOf(true));
                break;
            case 20:
                style.func_150228_d(Boolean.valueOf(true));
                break;
            case 21:
                style.func_150217_b(Boolean.valueOf(true));
                break;
            case 22:
                style.func_150238_a(null);
                style.func_150237_e(null);
                style.func_150227_a(null);
                style.func_150225_c(null);
                style.func_150228_d(null);
                style.func_150217_b(null);
        }
    }

    private static void createChatComponent(IChatComponent parent, String piece)
    {
        int formatIdx = piece.indexOf('§');
        if (formatIdx >= 0)
        {
            if (formatIdx != 0) {
                parent.func_150257_a(new ChatComponentText(piece.substring(0, formatIdx)));
            }
            ChatStyle style = new ChatStyle();
            String formattedPiece = piece.substring(formatIdx);
            for (int codePos = 1; codePos < formattedPiece.length(); codePos++)
            {
                applyStyleFormat(style, getFormattingFromChar(formattedPiece.charAt(codePos)));
                if ((codePos + 2 < formattedPiece.length()) && (formattedPiece.charAt(++codePos) != '§')) {
                    break;
                }
            }
            String rest = formattedPiece.substring(codePos);
            int endFormatIdx = rest.indexOf('§');
            boolean childParent = false;
            if (endFormatIdx >= 0)
            {
                if ((endFormatIdx + 2 <= rest.length()) &&
                        (rest.charAt(endFormatIdx + 1) != 'r'))
                {
                    childParent = true;
                    endFormatIdx--;
                }
            }
            else {
                endFormatIdx = rest.length();
            }
            ChatComponentText newChild = new ChatComponentText(rest.substring(0, endFormatIdx));
            newChild.func_150255_a(style);

            parent.func_150257_a(newChild);
            if (endFormatIdx < rest.length() - 1) {
                createChatComponent(childParent ? newChild : parent, rest.substring(endFormatIdx));
            }
        }
        else
        {
            parent.func_150257_a(new ChatComponentText(piece));
        }
    }

    public static void printLocalizedMessage(String key, Object... extraItems)
    {
        ArrayList<IChatComponent> translatedPieces = Lists.newArrayList(new ChatComponentTranslation(key, extraItems));

        ChatComponentText finalMessage = new ChatComponentText("");

        StringBuilder translatedMessage = new StringBuilder();
        for (IChatComponent chatPiece : translatedPieces) {
            translatedMessage.append(chatPiece.func_150261_e());
        }
        createChatComponent(finalMessage, translatedMessage.toString());

        Minecraft.func_71410_x().field_71439_g.func_145747_a(finalMessage);
    }

}
