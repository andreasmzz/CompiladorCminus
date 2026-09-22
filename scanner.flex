import java_cup.runtime.Symbol; 
 
%% 

%{
    private Symbol token(int tipo, String valor) {
        return new Symbol(tipo, yyline + 1, yyline + 1, valor);
    }
%}
 
%class Scanner 
%cup 
%full 
%line 
%char 
%eofval{ 
    return new Symbol(Tokens.EOF, new String("Fim do arquivo")); 
%eofval} 
%% 
 
/* Espaços e comentários */ 
[ \t\r\n]+         { /* ignora */ } 
"//"[^\r\n]*        { /* ignora comentári */ } 
"/*"([^*]|\*+[^*/])*\*+"/"             { /* ignora bloco de comentário */ } 
 
/* Palavras-chave */ 
else                        { return token(Tokens.ELSE, yytext()); } 
exit                        { return token(Tokens.EXIT, yytext()); } 
float                       { return token(Tokens.FLOAT, yytext()); } 
if                          { return token(Tokens.IF, yytext()); } 
int                         { return token(Tokens.INT, yytext()); } 
read                        { return token(Tokens.READ, yytext()); } 
return                      { return token(Tokens.RETURN, yytext()); } 
while                       { return token(Tokens.WHILE, yytext()); } 
write                       { return token(Tokens.WRITE, yytext()); } 
 
/* Operadores lógicos */ 
"&&"                        { return token(Tokens.AND, yytext()); } 
"||"                        { return token(Tokens.OR, yytext()); } 
"!"                         { return token(Tokens.NOT, yytext()); } 
 
/* Operadores relacionais */ 
"=="                        { return token(Tokens.EQ, yytext()); } 
"!="                        { return token(Tokens.NE, yytext()); } 
">="                        { return token(Tokens.GE, yytext()); } 
">"                         { return token(Tokens.GT, yytext()); } 
"<="                        { return token(Tokens.LE, yytext()); } 
"<"                         { return token(Tokens.LT, yytext()); } 
 
/* Operadores aritméticos */ 
"+"                         { return token(Tokens.PLUS, yytext()); } 
"-"                         { return token(Tokens.MINUS, yytext()); } 
"*"                         { return token(Tokens.TIMES, yytext()); } 
"/"                         { return token(Tokens.DIVIDE, yytext()); } 
 
/* Atribuição */ 
"="                         { return token(Tokens.ASSIGN, yytext()); } 
 
/* Delimitadores */ 
","                         { return token(Tokens.CM, yytext()); } 
";"                         { return token(Tokens.SC, yytext()); } 
"{"                         { return token(Tokens.LBR, yytext()); } 
"}"                         { return token(Tokens.RBR, yytext()); } 
"["                         { return token(Tokens.LBK, yytext()); } 
"]"                         { return token(Tokens.RBK, yytext()); } 
"("                         { return token(Tokens.LP, yytext()); } 
")"                         { return token(Tokens.RP, yytext()); } 
"'"                         { return token(Tokens.SQ, yytext()); } 
 
/* Constantes */ 
[0-9]+                      { return token(Tokens.INTCON, yytext()); } 
[0-9]+"."[0-9]+             { return token(Tokens.FLOATCON, yytext()); } 
"'"[a-zA-Z0-9 ]*"'"          { return token(Tokens.STRING, yytext()); } 
 
/* Identificadores */ 
[a-zA-Z][a-zA-Z0-9]*        { return token(Tokens.IDENTIFIER, yytext()); } 
 
/* Erro */ 
.                           { return token(Tokens.ERROR, "Caractere inválido: " + yytext()); }