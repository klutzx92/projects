import nn

class PerceptronModel(object):
    def __init__(self, dimensions):
        """
        Initialize a new Perceptron instance.

        A perceptron classifies data points as either belonging to a particular
        class (+1) or not (-1). `dimensions` is the dimensionality of the data.
        For example, dimensions=2 would mean that the perceptron must classify
        2D points.
        """
        self.w = nn.Parameter(1, dimensions)

    def get_weights(self):
        """
        Return a Parameter instance with the current weights of the perceptron.
        """
        return self.w

    def run(self, x):
        """
        Calculates the score assigned by the perceptron to a data point x.

        Inputs:
            x: a node with shape (1 x dimensions)
        Returns: a node containing a single number (the score)
        """
        "*** YOUR CODE HERE ***"
        weights = self.get_weights()
        feature_input = x
        return nn.DotProduct(feature_input, weights)

    def get_prediction(self, x):
        """
        Calculates the predicted class for a single data point `x`.

        Returns: 1 or -1
        """
        "*** YOUR CODE HERE ***"
        score = nn.as_scalar(self.run(x))
        if score >= 0:
            return 1
        else:
            return -1

    def train(self, dataset):
        """
        Train the perceptron until convergence.
        """
        "*** YOUR CODE HERE ***"
        while True:
            mistakes_made = 0
            weights = self.get_weights()
            batch_size = 1
            for x, y in dataset.iterate_once(batch_size):
                multiplier = nn.as_scalar(y)
                predicted_class = self.get_prediction(x)
                if predicted_class != multiplier:
                    weights.update(x, multiplier)
                    mistakes_made += 1
            if not mistakes_made:
                break

class RegressionModel(object):
    """
    A neural network model for approximating a function that maps from real
    numbers to real numbers. The network should be sufficiently large to be able
    to approximate sin(x) on the interval [-2pi, 2pi] to reasonable precision.
    """
    def __init__(self):
        # Initialize your model parameters here
        "*** YOUR CODE HERE ***"

        # For RELU: x has size (b x i),  W1=m1 has size (i x h), b1 has size (1 x h).
        # i: input size, h: hidden size
        # W2 has size (h x outputSize) and b2 has size(1 x outputSize)

        # So for us:
        # sin(x) only has one input (i=1), as well as one output(outputsize = 1)
        # relu(x*m1 + b1)*m2 + b2
        # x has size (batch_size x i=1), m should have size (1 x something)
        # x * m should have size (batch_size x something).
        # So to add with b, b should have size (? x something)

        # For this question: use nn.SquareLoss as your loss.

        # Spec says that run() outputs a node with shape (batch_size x 1)
        # output_size should be batch_size

        self.learning_rate = -.001
        self.batch_size = 1
        self.hidden_size = 400
        self.m1 = nn.Parameter(1, self.hidden_size) # (i x h)
        self.b1 = nn.Parameter(1, self.hidden_size) # (1 x h)
        self.m2 = nn.Parameter(self.hidden_size, 1) # (h x outputsize)
        self.b2 = nn.Parameter(1, 1) # (1 x outputsize)

        # Architecture Log:
        # a:-.001   b:1     h:400

    def run(self, x):
        """
        Runs the model for a batch of examples.

        Inputs:
            x: a node with shape (batch_size x 1)
        Returns:
            A node with shape (batch_size x 1) containing predicted y-values
        """
        "*** YOUR CODE HERE ***"

        # relu(x*m1 + b1)*m2 + b2
        # weights = self.m1
        # feature_input = x

        xm1 = nn.Linear(x, self.m1) # x = feature_input, m = weight
        xm1b1 = nn.AddBias(xm1, self.b1)
        reluxm1b1 = nn.ReLU(xm1b1)
        relum2 = nn.Linear(reluxm1b1, self.m2)
        predicted_y = nn.AddBias(relum2, self.b2)
        return predicted_y

    def get_loss(self, x, y):
        """
        Computes the loss for a batch of examples.

        Inputs:
            x: a node with shape (batch_size x 1)
            y: a node with shape (batch_size x 1), containing the true y-values
                to be used for training
        Returns: a loss node
        """
        "*** YOUR CODE HERE ***"
        predicted_class = self.run(x)
        return nn.SquareLoss(predicted_class, y)

    def train(self, dataset):
        """
        Trains the model.
        """
        "*** YOUR CODE HERE ***"
        m1 = self.m1
        m2 = self.m2
        b1 = self.b1
        b2 = self.b2

        multiplier = self.learning_rate

        while True:
            for x, y in dataset.iterate_once(self.batch_size):

                loss = self.get_loss(x, y)
                grad_wrt_m1, grad_wrt_b1, grad_wrt_m2, grad_wrt_b2 = nn.gradients(loss, [m1, b1, m2, b2])

                m1.update(grad_wrt_m1, multiplier)
                b1.update(grad_wrt_b1, multiplier)
                m2.update(grad_wrt_m2, multiplier)
                b2.update(grad_wrt_b2, multiplier)

            if nn.as_scalar(loss) < 0.02:
                break


class DigitClassificationModel(object):
    """
    A model for handwritten digit classification using the MNIST dataset.

    Each handwritten digit is a 28x28 pixel grayscale image, which is flattened
    into a 784-dimensional vector for the purposes of this model. Each entry in
    the vector is a floating point number between 0 and 1.

    The goal is to sort each digit into one of 10 classes (number 0 through 9).

    (See RegressionModel for more information about the APIs of different
    methods here. We recommend that you implement the RegressionModel before
    working on this part of the project.)
    """
    def __init__(self):
        # Initialize your model parameters here
        "*** YOUR CODE HERE ***"
        self.learning_rate = -.05
        self.batch_size = 100
        self.hidden_size = 100
        self.m1 = nn.Parameter(784, self.hidden_size) # (i x h)
        self.b1 = nn.Parameter(1, self.hidden_size) # (1 x h)
        self.m2 = nn.Parameter(self.hidden_size, 10) # (h x outputsize)
        self.b2 = nn.Parameter(1, 10) # (1 x outputsize)

        # ARCHITECTURE LOG:
        # 1.    a: -.001    b: 4    h: 400  way too slow
        # 2.    a: -.01     b: 100  h: 400  too slow, ended with 96.98% test accuracy
        # 3.    a: -.01     b: 100  h: 250  timed out (10 minute limit)
        # 4.    a: -.01     b: 100  h: 100  worked but took over 10 minutes.
        # 5.    a: -.05     b: 100  h: 100 worked!

    def run(self, x):
        """
        Runs the model for a batch of examples.

        Your model should predict a node with shape (batch_size x 10),
        containing scores. Higher scores correspond to greater probability of
        the image belonging to a particular class.

        Inputs:
            x: a node with shape (batch_size x 784)
        Output:
            A node with shape (batch_size x 10) containing predicted scores
                (also called logits)
        """
        "*** YOUR CODE HERE ***"
        xm1 = nn.Linear(x, self.m1) # x = feature_input, m = weight
        xm1b1 = nn.AddBias(xm1, self.b1)
        reluxm1b1 = nn.ReLU(xm1b1)
        relum2 = nn.Linear(reluxm1b1, self.m2)
        predicted_y = nn.AddBias(relum2, self.b2)
        return predicted_y # (batch_size x 10)

    def get_loss(self, x, y):
        """
        Computes the loss for a batch of examples.

        The correct labels `y` are represented as a node with shape
        (batch_size x 10). Each row is a one-hot vector encoding the correct
        digit class (0-9).

        Inputs:
            x: a node with shape (batch_size x 784)
            y: a node with shape (batch_size x 10)
        Returns: a loss node
        """
        "*** YOUR CODE HERE ***"
        predicted_class = self.run(x)
        return nn.SoftmaxLoss(predicted_class, y)


    def train(self, dataset):
        """
        Trains the model.
        """
        "*** YOUR CODE HERE ***"
        m1 = self.m1
        m2 = self.m2
        b1 = self.b1
        b2 = self.b2

        multiplier = self.learning_rate

        while True:
            for x, y in dataset.iterate_once(self.batch_size):

                loss = self.get_loss(x, y)
                grad_wrt_m1, grad_wrt_b1, grad_wrt_m2, grad_wrt_b2 = nn.gradients(loss, [m1, b1, m2, b2])

                m1.update(grad_wrt_m1, multiplier)
                b1.update(grad_wrt_b1, multiplier)
                m2.update(grad_wrt_m2, multiplier)
                b2.update(grad_wrt_b2, multiplier)

            if dataset.get_validation_accuracy() > .975:
                break

class LanguageIDModel(object):
    """
    A model for language identification at a single-word granularity.

    (See RegressionModel for more information about the APIs of different
    methods here. We recommend that you implement the RegressionModel before
    working on this part of the project.)
    """
    def __init__(self):
        # Our dataset contains words from five different languages, and the
        # combined alphabets of the five languages contain a total of 47 unique
        # characters.
        # You can refer to self.num_chars or len(self.languages) in your code
        self.num_chars = 47
        self.languages = ["English", "Spanish", "Finnish", "Dutch", "Polish"]

        # Initialize your model parameters here
        "*** YOUR CODE HERE ***"
        # hidden size should be subsequently large (d in specs)

        self.learning_rate = -.06   # a
        self.batch_size = 128       # b
        self.hidden_size = 40      # h

        self.w = nn.Parameter(self.num_chars, self.hidden_size)
        self.b0 = nn.Parameter(1,self.hidden_size)
        self.wHidden = nn.Parameter(self.hidden_size, self.hidden_size)
        self.b1 = nn.Parameter(1, self.hidden_size)
        self.wOutput = nn.Parameter(self.hidden_size, 5)
        self.b2 = nn.Parameter(1, 5)



        # self.m1 = nn.Parameter(784, self.hidden_size) # (i x h)
        # self.b1 = nn.Parameter(1, self.hidden_size) # (1 x h)
        # self.m2 = nn.Parameter(self.hidden_size, 10) # (h x outputsize)
        # self.b2 = nn.Parameter(1, 10) # (1 x outputsize)

        # ARCHITECTURE LOG:
        # 1.    a: -.1      b: 100    h: 400  hovered around 78% and timed out (limit ~5 minutes)
        # 2.    a: -.1      b: 80     h: 400  returned NaN or inf (learning rate too high?)
        # 3.    a: -.05     b: 80     h: 400  hovered around 79% and timed out

        # adding another layer (Relu + b2)
        # 4.    a: -.05     b: 100      h: 400  hovered around 81%
        # 5.    a: -.07     b: 128      h: 400  hovered around 82%
        # 6.    a: -.09     b: 128      h: 400  returned NaN or inf
        # 7.    a: -.09     b: 128      h: 100  returned NaN or inf
        # 8.    a: -.05     b: 128      h: 100  hovered around 82%
        # 9.    a: -.05     b: 128      h: 256  hovered around 20%
        # 10.   a: -.05     b: 100      h: 256  hovered around 20%
        # 11.   a: -.05     b: 100      h: 40  hovered around 20%   timed out with test accuracy 78%
        # 12.   a: -.05     b: 124      h: 40  hovered around 20%   timed out with test accuracy 78%
        # 13.   a: -.05     b: 128      h: 40  hovered around 20%   timed out with test accuracy 78%
        # 14.   a: -.06     b: 128      h: 40  hovered around 20%   timed out with test accuracy 78%

        # adding another RELU to second layer
        # 15.   a: -.06     b: 128      h: 40 DONE!




    def run(self, xs):
        """
        Runs the model for a batch of examples.

        Although words have different lengths, our data processing guarantees
        that within a single batch, all words will be of the same length (L).

        Here `xs` will be a list of length L. Each element of `xs` will be a
        node with shape (batch_size x self.num_chars), where every row in the
        array is a one-hot vector encoding of a character. For example, if we
        have a batch of 8 three-letter words where the last word is "cat", then
        xs[1] will be a node that contains a 1 at position (7, 0). Here the
        index 7 reflects the fact that "cat" is the last word in the batch, and
        the index 0 reflects the fact that the letter "a" is the inital (0th)
        letter of our combined alphabet for this task.

        Your model should use a Recurrent Neural Network to summarize the list
        `xs` into a single node of shape (batch_size x hidden_size), for your
        choice of hidden_size. It should then calculate a node of shape
        (batch_size x 5) containing scores, where higher scores correspond to
        greater probability of the word originating from a particular language.

        Inputs:
            xs: a list with L elements (one per character), where each element
                is a node with shape (batch_size x self.num_chars)
        Returns:
            A node with shape (batch_size x 5) containing predicted scores
                (also called logits)
        """
        "*** YOUR CODE HERE ***"
        initialx = xs[0]
        rest = xs[1:]

        def firstNet(xi):
            xw = nn.Linear(xi, self.w)
            xwb0 = nn.AddBias(xw, self.b0)
            return nn.ReLU(xwb0)

        def secondNet(hi):
            hWhidden = nn.Linear(hi, self.wHidden)
            return nn.ReLU(nn.AddBias(hWhidden, self.b1))

        hi = firstNet(initialx)

        for xi in rest:
            hi = nn.Add(firstNet(xi), secondNet(hi)) # also add Relu(nn.Add...?) + b of size (1 x h)?
            # hi is of size (batch_size x hidden_size)

        return nn.ReLU(nn.AddBias(nn.Linear(hi, self.wOutput), self.b2))

    def get_loss(self, xs, y):
        """
        Computes the loss for a batch of examples.

        The correct labels `y` are represented as a node with shape
        (batch_size x 5). Each row is a one-hot vector encoding the correct
        language.

        Inputs:
            xs: a list with L elements (one per character), where each element
                is a node with shape (batch_size x self.num_chars)
            y: a node with shape (batch_size x 5)
        Returns: a loss node
        """
        "*** YOUR CODE HERE ***"
        predicted_class = self.run(xs)
        return nn.SoftmaxLoss(predicted_class, y)   # which loss function do we use?

    def train(self, dataset):
        """
        Trains the model.
        """
        "*** YOUR CODE HERE ***"
        w = self.w
        b0 = self.b0
        wHidden = self.wHidden
        b1 = self.b1
        wOutput = self.wOutput
        b2 = self.b2

        multiplier = self.learning_rate

        while True:
            for x, y in dataset.iterate_once(self.batch_size):
                loss = self.get_loss(x, y)
                grad_wrt_w, grad_wrt_b0, grad_wrt_wHidden, grad_wrt_b1, grad_wrt_wOutput, grad_wrt_b2 = nn.gradients(loss, [w, b0, wHidden, b1, wOutput, b2])

                w.update(grad_wrt_w, multiplier)
                b0.update(grad_wrt_b0, multiplier)
                wHidden.update(grad_wrt_wHidden, multiplier)
                b1.update(grad_wrt_b1, multiplier)
                wOutput.update(grad_wrt_wOutput, multiplier)
                b2.update(grad_wrt_b2, multiplier)

            if dataset.get_validation_accuracy() > .85:
                break
