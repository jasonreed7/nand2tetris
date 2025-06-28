// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

(LOOP)

@KBD
D=M

@UNPRESSED
D;JEQ

@pixel
M=-1

@DRAW_START
0;JMP

(UNPRESSED)

@pixel
M=0

(DRAW_START)

@i
M=0

(DRAW_LOOP)

@SCREEN
D=A

@i
D=D+M

@ptr
M=D

@pixel
D=M

@ptr
A=M

M=D

@i
M=M+1

D=M

@8192
D=D-A

@LOOP
D;JGE

@DRAW_LOOP
0;JMP