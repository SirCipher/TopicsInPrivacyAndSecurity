dtmc

const int N;

const double pwrite = 0.001*N*N;
const double pnotice;
const double pguess = (N=4)?0.0001:((N=5)?0.00001:0.000001);
const double pretry;

module PIN_attack
  state : [0..5] init 0;

  [start]   state=0 -> pwrite:(state'=1) + (1-pwrite):(state'=2);
  [written] state=1 -> pnotice:(state'=3) + (1-pnotice):(state'=2);
  [guess]   state=2 -> pguess:(state'=3) + (1-pguess):(state'=4);
  [success] state=3 -> 1:(state'=3);
  [wrong]   state=4 -> pretry:(state'=2) + (1-pretry):(state'=5);
  [fail]    state=5 -> 1:(state'=5);
endmodule 