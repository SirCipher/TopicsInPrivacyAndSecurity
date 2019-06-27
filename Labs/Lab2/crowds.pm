dtmc

const int N=8;
const double pforward; //=0.75;
const double c;
const int NPaths;

module crowds
  path : [0..NPaths] init 0;    // paths established so far
  currentParty : [1..N] init 1; // (without loss of generality) we assume the originator has ID 1
  log1: [0..NPaths] init 0;     // times a dishonest party noticed request coming from 1
  log2: [0..NPaths] init 0;     // times a dishonest party noticed request coming from 2
  log3: [0..NPaths] init 0;     // times a dishonest party noticed request coming from 3
  log4: [0..NPaths] init 0;     // times a dishonest party noticed request coming from 4
  log5: [0..NPaths] init 0;     // times a dishonest party noticed request coming from 5
  log6: [0..NPaths] init 0;     // times a dishonest party noticed request coming from 6
  log7: [0..NPaths] init 0;     // times a dishonest party noticed request coming from 7
  log8: [0..NPaths] init 0;     // times a dishonest party noticed request coming from 8
  state : [0..4] init 0;
  
  // Decide if request is forwarded to another party or sent to destination  
  [start]   state=0 & path<NPaths -> pforward:(state'=1) + (1-pforward):(state'=4);
  // Stop if all paths were established
  [stop]    state=0 & path=NPaths -> (state'=0)&(path'=NPaths);

  // With probability c, the request is forwarded to a dishonest party
  [choose]  state=1 -> c:(state'=2) + (1-c):(state'=3);

  // The dishonest party logs the party it received the message from (also, jump to the next path, as we modelled what we need)
  [log]     state=2 & currentParty=1 -> 1:(log1'=min(NPaths,log1+1))&(state'=4);
  [log]     state=2 & currentParty=2 -> 1:(log2'=min(NPaths,log2+1))&(state'=4);
  [log]     state=2 & currentParty=3 -> 1:(log3'=min(NPaths,log3+1))&(state'=4);
  [log]     state=2 & currentParty=4 -> 1:(log4'=min(NPaths,log4+1))&(state'=4);
  [log]     state=2 & currentParty=5 -> 1:(log5'=min(NPaths,log5+1))&(state'=4);
  [log]     state=2 & currentParty=6 -> 1:(log6'=min(NPaths,log6+1))&(state'=4);
  [log]     state=2 & currentParty=7 -> 1:(log7'=min(NPaths,log7+1))&(state'=4);
  [log]     state=2 & currentParty=8 -> 1:(log8'=min(NPaths,log8+1))&(state'=4);

  // Forward to random party
  [forward] state=3 -> 1/N:(currentParty'=1)&(state'=0) +    
                       1/N:(currentParty'=2)&(state'=0) +    
                       1/N:(currentParty'=3)&(state'=0) +    
                       1/N:(currentParty'=4)&(state'=0) +    
                       1/N:(currentParty'=5)&(state'=0) +    
                       1/N:(currentParty'=6)&(state'=0) +    
                       1/N:(currentParty'=7)&(state'=0) +    
                       1/N:(currentParty'=8)&(state'=0);    

  // Finished this path, so move to the next
  [restart] state=4 -> (path'=min(NPaths,path+1))&(currentParty'=1)&(state'=0);
endmodule