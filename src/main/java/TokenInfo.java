/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author marco
 */
import java_cup.runtime.Symbol;

public class TokenInfo extends Symbol{
    public final int linha;
    public final int coluna;

    public TokenInfo(
        int sym,
        int linha,
        int coluna,
        Object value
    ) {
        super(sym, linha, coluna, value);

        this.linha = linha;
        this.coluna = coluna;
    }
}
