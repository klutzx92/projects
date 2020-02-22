# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
#
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        negativeInf = float("-inf")
        positiveInf = float("inf")

        evalScore = successorGameState.getScore()

        #distance of closest food pellet
        foods = newFood.asList()
        if foods:
            foodDistance = min([manhattanDistance(newPos, foods[i]) for i in range(len(foods))])
        else:
            foodDistance = 0
        evalScore -= foodDistance

        #number of foods left
        numFood = successorGameState.getNumFood()
        if numFood > 0:
            evalScore += 10000*(1/numFood)
        else:
            evalScore = positiveInf

        #position of closest ghost
        ghostPositions = successorGameState.getGhostPositions()
        closestGhostPos = None
        minimumGhostDistance = positiveInf
        for i in range(len(ghostPositions)):
            distance = manhattanDistance(newPos, ghostPositions[i])
            if distance < minimumGhostDistance:
                closestGhostPos = ghostPositions[i]
                minimumGhostDistance = distance
        evalScore += minimumGhostDistance

        #can't touch this
        if newPos == closestGhostPos:
            evalScore = negativeInf

        #stop Pacman from being a lazy bish
        if action == 'Stop':
            evalScore = negativeInf

        return evalScore

def scoreEvaluationFunction(currentGameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.

        Here are some method calls that might be useful when implementing minimax.

        gameState.getLegalActions(agentIndex):
        Returns a list of legal actions for an agent
        agentIndex=0 means Pacman, ghosts are >= 1

        gameState.generateSuccessor(agentIndex, action):
        Returns the successor game state after an agent takes an action

        gameState.getNumAgents():
        Returns the total number of agents in the game

        gameState.isWin():
        Returns whether or not the game state is a winning state

        gameState.isLose():
        Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"

        numAgents = gameState.getNumAgents()
        layers = numAgents * self.depth
        return self.value(gameState, self.index, 0)
        util.raiseNotDefined()

    def value(self, state, agentIndex, layer):
        layer += 1
        maxDepth = state.getNumAgents() * self.depth + 1
        if state.isLose() or state.isWin():
            return self.evaluationFunction(state)
        elif layer == maxDepth:
            return self.evaluationFunction(state)
        elif agentIndex == 0:
            return self.maxValue(state, agentIndex, layer)
        elif agentIndex > 0:
            return self.minValue(state, agentIndex, layer)
        return

    def maxValue(self, state, agentIndex, layer):
        v = float("-inf")
        actions = state.getLegalActions(agentIndex)
        retAction = None
        nextAgent = (agentIndex + 1) % state.getNumAgents()
        for action in actions:
            successorState = state.generateSuccessor(agentIndex, action)
            nextVal = self.value(successorState, nextAgent, layer)
            if nextVal > v:
                v = nextVal
                retAction = action
        if layer > 1:
            return v
        else:
            return retAction

    def minValue(self, state, agentIndex, layer):
        v = float("inf")
        actions = state.getLegalActions(agentIndex)
        retAction = None
        nextAgent = (agentIndex + 1) % state.getNumAgents()
        for action in actions:
            successorState = state.generateSuccessor(agentIndex, action)
            nextVal = self.value(successorState, nextAgent, layer)
            if nextVal < v:
                v = nextVal
                retAction = action
        if layer > 1:
            return v
        else:
            return retAction



class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        negInf = float("-inf")
        posInf = float("inf")

        return self.value(gameState, self.index, 0, negInf, posInf)

        util.raiseNotDefined()

    def value(self, state, agentIndex, layer, alpha, beta):
        layer += 1
        maxDepth = state.getNumAgents() * self.depth + 1
        if state.isLose() or state.isWin():
            return self.evaluationFunction(state)
        elif layer == maxDepth:
            return self.evaluationFunction(state)
        elif agentIndex == 0:
            return self.maxValue(state, agentIndex, layer, alpha, beta)
        elif agentIndex > 0:
            return self.minValue(state, agentIndex, layer, alpha, beta)
        return

    def maxValue(self, state, agentIndex, layer, alpha, beta):
        v = float("-inf")
        a = alpha
        b = beta
        actions = state.getLegalActions(agentIndex)
        retAction = None
        nextAgent = (agentIndex + 1) % state.getNumAgents()
        for action in actions:
            successorState = state.generateSuccessor(agentIndex, action)
            nextVal = self.value(successorState, nextAgent, layer, a, b)
            if nextVal > v:
                v = nextVal
                retAction = action
            if v > b:
                return v
            a = max(a, v)
        if layer > 1:
            return v
        else:
            return retAction

    def minValue(self, state, agentIndex, layer, alpha, beta):
        v = float("inf")
        a = alpha
        b = beta
        actions = state.getLegalActions(agentIndex)
        retAction = None
        nextAgent = (agentIndex + 1) % state.getNumAgents()
        for action in actions:
            successorState = state.generateSuccessor(agentIndex, action)
            nextVal = self.value(successorState, nextAgent, layer, a, b)
            if nextVal < v:
                v = nextVal
                retAction = action
            if v < a:
                return v
            b = min(b, v)
        if layer > 1:
            return v
        else:
            return retAction

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        "*** YOUR CODE HERE ***"
        return self.value(gameState, self.index, 0)
        util.raiseNotDefined()

    def value(self, state, agentIndex, layer):
        layer += 1
        maxDepth = state.getNumAgents() * self.depth + 1
        if state.isLose() or state.isWin():
            return self.evaluationFunction(state)
        elif layer == maxDepth:
            return self.evaluationFunction(state)
        elif agentIndex == 0:
            return self.maxValue(state, agentIndex, layer)
        elif agentIndex > 0:
            return self.expValue(state, agentIndex, layer)
        return

    def maxValue(self, state, agentIndex, layer):
        v = float("-inf")
        actions = state.getLegalActions(agentIndex)
        retAction = None
        nextAgent = (agentIndex + 1) % state.getNumAgents()
        for action in actions:
            successorState = state.generateSuccessor(agentIndex, action)
            nextVal = self.value(successorState, nextAgent, layer)
            if nextVal > v:
                v = nextVal
                retAction = action
        if layer > 1:
            return v
        else:
            return retAction

    def expValue(self, state, agentIndex, layer):
        v = 0
        actions = state.getLegalActions(agentIndex)
        p = 1/len(actions)
        retAction = None
        nextAgent = (agentIndex + 1) % state.getNumAgents()
        for action in actions:
            successorState = state.generateSuccessor(agentIndex, action)
            expected = self.value(successorState, nextAgent, layer)
            v += p * expected
        return v

def betterEvaluationFunction(currentGameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    #can extract from currentGameState:
    #isWin(), isLose(), getNumAgents(), getLegalActions(), getPacmanPosition(),
    #getGhostPosition(s) (), getScore(), getCapsules(), getNumFood(), getFood().asList(),

    # currentGameState.isWin()
    # currentGameState.isLose()
    # currentGameState.getLegalActions()
    # currentGameState.getPacmanPosition()
    # currentGameState.getGhostPositions()
    # currentGameState.getScore()
    # currentGameState.getCapsules()
    # currentGameState.getNumFood()
    # currentGameState.getFood().asList()

    evalScore = currentGameState.getScore()
    pacmanPos = currentGameState.getPacmanPosition()
    ghostPositions = currentGameState.getGhostPositions()
    numFood = currentGameState.getNumFood()
    foods = currentGameState.getFood().asList()
    numAgents = currentGameState.getNumAgents()
    numGhosts = numAgents - 1
    capsules = currentGameState.getCapsules()
    ghostStates = currentGameState.getGhostStates()

    #start
    negativeInf = float("-inf")
    positiveInf = float("inf")

    if currentGameState.isWin():
        return positiveInf
    elif currentGameState.isLose():
        return negativeInf

    #distance to closest food pellet
    if foods:
        foodDistance = min([manhattanDistance(pacmanPos, foods[i]) for i in range(len(foods))])
    else:
        foodDistance = 0
    evalScore -= foodDistance

    #number of foods left
    if numFood == 0:
        evalScore = positiveInf
    else:
        evalScore += 10000*(1/numFood)


    #ghosts
    ghostSumDistance = 0
    closestGhost = None
    minimumGhostDistance = positiveInf
    for ghostState in ghostStates:
        ghostPos = ghostState.getPosition()
        distance = manhattanDistance(pacmanPos, ghostPos)
        ghostSumDistance += distance
        if distance < minimumGhostDistance:
            closestGhost = ghostState
            minimumGhostDistance = distance

    if not any([ghostState.scaredTimer for ghostState in ghostStates]):
        #evalScore += ghostSumDistance
        #capsules
        if capsules:
            capDistance = min([manhattanDistance(pacmanPos, capsule) for capsule in capsules])
            if capDistance + minimumGhostDistance < 40:
                evalScore -= capDistance
    else:
        if closestGhost.scaredTimer > minimumGhostDistance:
            evalScore -= minimumGhostDistance


    #stay away
    if pacmanPos == closestGhost.getPosition():
        if closestGhost.scaredTimer == 0:
            evalScore = negativeInf
        else:
            evalScore += 100



    return evalScore


    util.raiseNotDefined()

# Abbreviation
better = betterEvaluationFunction
