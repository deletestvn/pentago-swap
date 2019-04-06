package student_player;

import pentago_swap.PentagoMove;
import pentago_swap.PentagoBoardState;

import java.util.ArrayList;
import java.util.AbstractMap;

import boardgame.Board;

/**
 * 
 * @author Steven Wang (260728358)
 * 
 */

public class MyTools {
    
	public static double getSomething() {
        return Math.random();
    }
	    
	public static PentagoMove getMove(PentagoBoardState pBoardState, int player){
		
		AbstractMap.SimpleEntry<Integer, PentagoMove> bestHeuristicMove;
		
		bestHeuristicMove = AlphaBetaPruning(3, pBoardState, player, Integer.MIN_VALUE, Integer.MAX_VALUE);
		//bestHeuristicMove = MiniMax(2, pBoardState, player);
		
		PentagoMove bestMove = bestHeuristicMove.getValue();
		
		//int bestHeuristic = bestHeuristicMove.getKey();
		//System.out.println("My best heuristic is:" + bestHeuristic);
		
		return bestMove;
	}
	
	public static AbstractMap.SimpleEntry<Integer, PentagoMove> AlphaBetaPruning(int depth, PentagoBoardState pBoardState, int player, int alpha, int beta){
		// assume player is white, if so return alpha, else return beta
		int currentPlayer = pBoardState.getTurnPlayer();
		ArrayList<PentagoMove> possibleMoves = pBoardState.getAllLegalMoves();
		
		int bestHeuristic = Heuristic(pBoardState, player);
		PentagoMove bestMove = possibleMoves.get(0);
		
		AbstractMap.SimpleEntry<Integer, PentagoMove> bestHeuristicMove;
		
		if(possibleMoves.isEmpty() || depth == 0){
			bestHeuristicMove = new AbstractMap.SimpleEntry<>(bestHeuristic, bestMove);
			return bestHeuristicMove;
		}
		
		else{
			for(PentagoMove pMove: possibleMoves){
				PentagoBoardState pBoardState_moved = (PentagoBoardState)pBoardState.clone();
				pBoardState_moved.processMove(pMove);
				
				if(currentPlayer == player){
					bestHeuristic = AlphaBetaPruning(depth-1, pBoardState_moved, player, alpha, beta).getKey();
					if(bestHeuristic > alpha){
						alpha = bestHeuristic;
						bestMove = pMove;
					}
				}
				else{
					bestHeuristic = AlphaBetaPruning(depth-1, pBoardState_moved, player, alpha, beta).getKey();
					if(bestHeuristic < beta){
						beta = bestHeuristic;
						bestMove = pMove;
					}
				}
				if(alpha >= beta){
					break;
				}
			}
			
			if(player == currentPlayer){
				bestHeuristicMove = new AbstractMap.SimpleEntry<>(alpha, bestMove);				
			}
			else{
				bestHeuristicMove = new AbstractMap.SimpleEntry<>(beta, bestMove);
			}
			return bestHeuristicMove;
		}
	}
	
	public static AbstractMap.SimpleEntry<Integer, PentagoMove> MiniMax(int depth, PentagoBoardState pBoardState, int player){
		
		ArrayList<PentagoMove> possibleMoves = pBoardState.getAllLegalMoves();
		int currentPlayer = pBoardState.getTurnPlayer();
		int bestHeuristic = Heuristic(pBoardState, player);
		PentagoMove bestMove = possibleMoves.get(0);
		
		AbstractMap.SimpleEntry<Integer, PentagoMove> bestHeuristicMove;
				
		int tempHeuristic;
		
		if(possibleMoves.isEmpty() || depth == 0){
			bestHeuristicMove = new AbstractMap.SimpleEntry<>(bestHeuristic, bestMove);
			return bestHeuristicMove;
		}
		else{
			if(currentPlayer == player){
				bestHeuristic = Integer.MIN_VALUE;
				for(PentagoMove pMove: possibleMoves){
					PentagoBoardState pBoardState_moved = (PentagoBoardState)pBoardState.clone();
					pBoardState_moved.processMove(pMove);
					tempHeuristic = MiniMax(depth-1, pBoardState_moved, player).getKey();
					if(tempHeuristic > bestHeuristic){
						bestHeuristic = tempHeuristic;
						bestMove = pMove;
					}				
				}
			}
			else{
				bestHeuristic = Integer.MAX_VALUE;
				for(PentagoMove pMove: possibleMoves){
					PentagoBoardState pBoardState_moved = (PentagoBoardState)pBoardState.clone();
					pBoardState_moved.processMove(pMove);
					tempHeuristic = MiniMax(depth-1, pBoardState_moved, player).getKey();
					if(tempHeuristic < bestHeuristic){
						bestHeuristic = tempHeuristic;
						bestMove = pMove;
					}
				}
			}	
		}
		
		bestHeuristicMove = new AbstractMap.SimpleEntry<>(bestHeuristic, bestMove);
		return bestHeuristicMove;
	}
    
	/**
	 * Check the heuristic value of give board state
	 * @param pBoardState: the current board state
	 * @param player: the AI
	 * @return hValue: the overall heuristic value
	 */
	
	public static int Heuristic(PentagoBoardState pBoardState, int player){
		
		/**
		 * 
		 * Key rules of this game is to block opponent's winning move and take player's winning move
		 * 1. If there is a opponent's winning, set heuristic to MIN 
		 * 2. If there is a player's winning, set heuristic to MAX (when 1 is false)
		 * 3. Otherwise, calculate continuous pieces
		 */
		
		int hValue = 0;
		int winner = pBoardState.getWinner();
		
		if(winner == PentagoBoardState.WHITE){
			hValue = Integer.MAX_VALUE;
		}
		else if(winner == PentagoBoardState.BLACK){
			hValue = Integer.MIN_VALUE;
		}
		else{
			/** 
			 * 
			 * Assume the player is WHITE at this stage, inverse the Value if it is not
			 * For each horizontal line, vertical line, diagonal line:
			 * If there are 2 continuous same color piece, add/deduct 10
			 * If there are 3 continuous same color piece, add/deduct 100
			 * If there are 4 continuous same color piece, add/deduct 1000
			 * Reset the continuousCount to 0 when there is a break
			 */
			
			// Horizontal Line
			for(int i=0; i<6; i++){
				int continuousCount = 0;
				for(int j=0; j<5; j++){
					if(pBoardState.getPieceAt(i,j) == PentagoBoardState.Piece.WHITE && pBoardState.getPieceAt(i,j+1) == PentagoBoardState.Piece.WHITE){
						continuousCount++;
						hValue += Math.pow(10,continuousCount);
					}
					else if(pBoardState.getPieceAt(i,j) == PentagoBoardState.Piece.BLACK && pBoardState.getPieceAt(i,j+1) == PentagoBoardState.Piece.BLACK){
						continuousCount++;
						hValue -= Math.pow(10,continuousCount);
					}
					else{
						continuousCount = 0;
					}
				}
			}
			
			// Vertical Line
			for(int j=0; j<6; j++){
				int continuousCount = 0;
				for(int i=0; i<5; i++){
					if(pBoardState.getPieceAt(i,j) == PentagoBoardState.Piece.WHITE && pBoardState.getPieceAt(i+1,j) == PentagoBoardState.Piece.WHITE){
						continuousCount++;
						hValue += Math.pow(10, continuousCount);
					}
					else if(pBoardState.getPieceAt(i,j) == PentagoBoardState.Piece.BLACK && pBoardState.getPieceAt(i+1,j) == PentagoBoardState.Piece.BLACK){
						continuousCount++;
						hValue -= Math.pow(10, continuousCount);
					}
					else{
						continuousCount = 0;
					}
				}
			}
					
			// Diagonal Line
			int posContinuousCount = 0;
			int negContinuousCount = 0;
			
			for(int i=0; i<5; i++){
				// (0,0) to (5,5)
				if(pBoardState.getPieceAt(i,i) == PentagoBoardState.Piece.WHITE && pBoardState.getPieceAt(i+1,i+1) == PentagoBoardState.Piece.WHITE){
					posContinuousCount++;
					hValue += Math.pow(10, posContinuousCount);
				}
				else if(pBoardState.getPieceAt(i,i) == PentagoBoardState.Piece.BLACK && pBoardState.getPieceAt(i+1,i+1) == PentagoBoardState.Piece.BLACK){
					posContinuousCount++;
					hValue -= Math.pow(10, posContinuousCount);
				}
				else{
					posContinuousCount = 0;
				}
				
				// (0,5) to (5,0)
				if(pBoardState.getPieceAt(i,5-i) == PentagoBoardState.Piece.WHITE && pBoardState.getPieceAt(i+1,4-i) == PentagoBoardState.Piece.WHITE){
					negContinuousCount++;
					hValue += Math.pow(10, negContinuousCount);
				}
				else if(pBoardState.getPieceAt(i,5-i) == PentagoBoardState.Piece.BLACK && pBoardState.getPieceAt(i+1,4-i) == PentagoBoardState.Piece.BLACK){
					negContinuousCount++;
					hValue -= Math.pow(10, negContinuousCount);
				}
				else{
					negContinuousCount = 0;
				}
				
				//
			}
		}
		
		if(player == PentagoBoardState.BLACK){
			hValue = -hValue;
		}
		return hValue;
	}
}