package org.ggp.base.player.gamer.statemachine;

import java.util.ArrayList;
import java.util.List;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

public final class MyMonteCarloGamer extends StateMachineGamer {

	private int depthLimit = 1;
	private int depthLevel = 0;
	long finishTime;
	private int[] depth = new int[1];

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		// TODO Auto-generated method stub

	}

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {

		long startTime = System.currentTimeMillis();
		finishTime = timeout - 1000 ;
		depthLevel = 0;
		StateMachine machine = getStateMachine();
		List<Move> moves = machine.getLegalMoves(getCurrentState(), getRole());
		Move move = moves.get(0);
		if (moves.size() > 1){
			move = findBestMove(moves ,getCurrentState());
		}
		long stopTime = System.currentTimeMillis();

		notifyObservers(new GamerSelectedMoveEvent(moves, move, stopTime - startTime));
		return move;
	}

	private Role findOpponent(Role player){
		Role opponent = null;
		for(int i = 0 ; i < getStateMachine().getRoles().size(); i++){
			if(!getStateMachine().getRoles().get(i).equals(player)){
				opponent = getStateMachine().getRoles().get(i);
			}
		}
		return opponent;
	}

	private Move findBestMove(List<Move> moves, MachineState state)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException{
		Move bestMove = moves.get(0);
		int moveScore = 0;
		for (int i = 0 ; i < moves.size(); i++){
			if (System.currentTimeMillis() > finishTime){
				System.out.println("ERROR");
				return bestMove;
			}
			int result = findMinScore(moves.get(i), state , depthLevel);
			if (result == 100){
				bestMove = moves.get(i);
				return bestMove;
			}
			if (result > moveScore){
				moveScore = result;
				bestMove = moves.get(i);
			}
		}
		return bestMove;
	}

	private int findMinScore(Move move, MachineState state, int level)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException{
			StateMachine machine = getStateMachine();
			Role opponent = findOpponent(getRole());
			List<Move> moves = machine.getLegalMoves(state, opponent);
			int score = 100;
			for (int i = 0; i < moves.size(); i++){
			List<Move> attemptMove = new ArrayList<Move>();
			for (int j = 0 ;j < machine.getRoles().size(); j++){
				if (machine.getRoles().get(j).equals(getRole())){
					attemptMove.add(move) ;
				}
				else{
					attemptMove.add(moves.get(i));
					}
				}
				MachineState nextState = machine.findNext(attemptMove, state);
				int result = findMaxScore(nextState , level++);
				if (result == 0) {
					score = result;
					return score;
					}
				if (result < score){
					score = result;
				}
			}
		return score;
	}

	private int findMaxScore(MachineState state, int level)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException{
			StateMachine machine = getStateMachine();
			int maxScore = 0;
			if(machine.isTerminal(state)){
				maxScore = machine.findReward(getRole(), state);
				return maxScore;
			}
			else if (level >= depthLimit) {
				return monteCarlo(state, getRole(),4);
			}
			else{
				List<Move> moves = machine.getLegalMoves(state, getRole());
				for (int i = 0; i < moves.size(); i++){
					int result = findMinScore(moves.get(i), state , level);
					if (result == 100) {
						maxScore = 100;
						return maxScore;
						}
					if (maxScore > result){
						maxScore = result;
					}
				}
			}
			return maxScore;
		}

	private int monteCarlo(MachineState state, Role role , int count){
		StateMachine machine = getStateMachine();
		int total = 0;
		for (int i = 0 ; i < count ; i++){
			try{
				MachineState finalState = machine.performDepthCharge(state, depth);
				total += machine.findReward(role, finalState);
			}
			catch (Exception e){
	            e.printStackTrace();
	            return 0;
			}
		}
		return (total / count);
	}


	@Override
	public void stateMachineStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stateMachineAbort() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preview(Game g, long timeout) throws GamePreviewException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "MonteCarloGamer";
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new SimpleDetailPanel();
	}

}
