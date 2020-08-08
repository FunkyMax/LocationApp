package bachelor.test.locationapp.presenter.positioning

import org.apache.commons.math3.filter.DefaultMeasurementModel
import org.apache.commons.math3.filter.DefaultProcessModel
import org.apache.commons.math3.filter.KalmanFilter
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import kotlin.math.pow

// For every 100ms when a new UWB location result comes in, we want the Kalman Filter to calculate the best estimate.
private const val TIME_DELTA = 0.1
// Studies reveal that the highest acceleration change for pedestrians lies between 0.7m/s**2
// and 1.4m/s**2.
// For tweaking reasons, the best MAX_ACCELERATION value still has to be determined empirically.
private const val MAX_ACCELERATION = 0.8 * TIME_DELTA
// For the process covariance matrix to contain appropriate values, we have to multiply it with
// the variance of MAX_ACCELERATION which is the half to maximum change of the fastest changing variable (acceleration)
// for each time frame of 100ms.
private const val MAX_ACCELERATION_VARIANCE = MAX_ACCELERATION * MAX_ACCELERATION

class KalmanFilterConfigurator {
    // x
    private val stateVector = ArrayRealVector(9)
    // F
    private val stateTransitionMatrix = Array2DRowRealMatrix(9, 9)
    // B
    private val controlMatrix = Array2DRowRealMatrix(9, 1)
    // Q
    private val processNoiseMatrix = Array2DRowRealMatrix(9, 9)
    // P
    private val stateEstimateNoiseMatrix = Array2DRowRealMatrix(9, 9)
    // H
    private val measurementMatrix = Array2DRowRealMatrix(6, 9)
    // R
    private val measurementNoiseMatrix = Array2DRowRealMatrix(6, 6)

    private lateinit var processModel: DefaultProcessModel
    private lateinit var measurementModel: DefaultMeasurementModel

    fun configureKalmanFilter(initialLocation: LocationData): KalmanFilter {
        processModel = configureProcessModel(initialLocation)
        measurementModel = configureMeasurementModel()
        return KalmanFilter(processModel, measurementModel)
    }

    private fun configureProcessModel(initialLocation: LocationData): DefaultProcessModel {
        // x
        stateVector.setEntry(0, initialLocation.xPos.toDouble())
        stateVector.setEntry(1, initialLocation.yPos.toDouble())
        stateVector.setEntry(2, initialLocation.zPos.toDouble())
        stateVector.setEntry(3, 0.0)
        stateVector.setEntry(4, 0.0)
        stateVector.setEntry(5, 0.0)
        stateVector.setEntry(6, 0.0)
        stateVector.setEntry(7, 0.0)
        stateVector.setEntry(8, 0.0)

        // P
        stateEstimateNoiseMatrix.setEntry(0, 0, 1.0)
        stateEstimateNoiseMatrix.setEntry(0, 1, 0.0)
        stateEstimateNoiseMatrix.setEntry(0, 2, 0.0)
        stateEstimateNoiseMatrix.setEntry(0, 3, 0.0)
        stateEstimateNoiseMatrix.setEntry(0, 4, 0.0)
        stateEstimateNoiseMatrix.setEntry(0, 5, 0.0)
        stateEstimateNoiseMatrix.setEntry(0, 6, 0.0)
        stateEstimateNoiseMatrix.setEntry(0, 7, 0.0)
        stateEstimateNoiseMatrix.setEntry(0, 8, 0.0)
        stateEstimateNoiseMatrix.setEntry(1, 0, 0.0)
        stateEstimateNoiseMatrix.setEntry(1, 1, 1.0)
        stateEstimateNoiseMatrix.setEntry(1, 2, 0.0)
        stateEstimateNoiseMatrix.setEntry(1, 3, 0.0)
        stateEstimateNoiseMatrix.setEntry(1, 4, 0.0)
        stateEstimateNoiseMatrix.setEntry(1, 5, 0.0)
        stateEstimateNoiseMatrix.setEntry(1, 6, 0.0)
        stateEstimateNoiseMatrix.setEntry(1, 7, 0.0)
        stateEstimateNoiseMatrix.setEntry(1, 8, 0.0)
        stateEstimateNoiseMatrix.setEntry(2, 0, 0.0)
        stateEstimateNoiseMatrix.setEntry(2, 1, 0.0)
        stateEstimateNoiseMatrix.setEntry(2, 2, 1.0)
        stateEstimateNoiseMatrix.setEntry(2, 3, 0.0)
        stateEstimateNoiseMatrix.setEntry(2, 4, 0.0)
        stateEstimateNoiseMatrix.setEntry(2, 5, 0.0)
        stateEstimateNoiseMatrix.setEntry(2, 6, 0.0)
        stateEstimateNoiseMatrix.setEntry(2, 7, 0.0)
        stateEstimateNoiseMatrix.setEntry(2, 8, 0.0)
        stateEstimateNoiseMatrix.setEntry(3, 0, 0.0)
        stateEstimateNoiseMatrix.setEntry(3, 1, 0.0)
        stateEstimateNoiseMatrix.setEntry(3, 2, 0.0)
        stateEstimateNoiseMatrix.setEntry(3, 3, 1.0)
        stateEstimateNoiseMatrix.setEntry(3, 4, 0.0)
        stateEstimateNoiseMatrix.setEntry(3, 5, 0.0)
        stateEstimateNoiseMatrix.setEntry(3, 6, 0.0)
        stateEstimateNoiseMatrix.setEntry(3, 7, 0.0)
        stateEstimateNoiseMatrix.setEntry(3, 8, 0.0)
        stateEstimateNoiseMatrix.setEntry(4, 0, 0.0)
        stateEstimateNoiseMatrix.setEntry(4, 1, 0.0)
        stateEstimateNoiseMatrix.setEntry(4, 2, 0.0)
        stateEstimateNoiseMatrix.setEntry(4, 3, 0.0)
        stateEstimateNoiseMatrix.setEntry(4, 4, 1.0)
        stateEstimateNoiseMatrix.setEntry(4, 5, 0.0)
        stateEstimateNoiseMatrix.setEntry(4, 6, 0.0)
        stateEstimateNoiseMatrix.setEntry(4, 7, 0.0)
        stateEstimateNoiseMatrix.setEntry(4, 8, 0.0)
        stateEstimateNoiseMatrix.setEntry(5, 0, 0.0)
        stateEstimateNoiseMatrix.setEntry(5, 1, 0.0)
        stateEstimateNoiseMatrix.setEntry(5, 2, 0.0)
        stateEstimateNoiseMatrix.setEntry(5, 3, 0.0)
        stateEstimateNoiseMatrix.setEntry(5, 4, 0.0)
        stateEstimateNoiseMatrix.setEntry(5, 5, 1.0)
        stateEstimateNoiseMatrix.setEntry(5, 6, 0.0)
        stateEstimateNoiseMatrix.setEntry(5, 7, 0.0)
        stateEstimateNoiseMatrix.setEntry(5, 8, 0.0)
        stateEstimateNoiseMatrix.setEntry(6, 0, 0.0)
        stateEstimateNoiseMatrix.setEntry(6, 1, 0.0)
        stateEstimateNoiseMatrix.setEntry(6, 2, 0.0)
        stateEstimateNoiseMatrix.setEntry(6, 3, 0.0)
        stateEstimateNoiseMatrix.setEntry(6, 4, 0.0)
        stateEstimateNoiseMatrix.setEntry(6, 5, 0.0)
        stateEstimateNoiseMatrix.setEntry(6, 6, 1.0)
        stateEstimateNoiseMatrix.setEntry(6, 7, 0.0)
        stateEstimateNoiseMatrix.setEntry(6, 8, 0.0)
        stateEstimateNoiseMatrix.setEntry(7, 0, 0.0)
        stateEstimateNoiseMatrix.setEntry(7, 1, 0.0)
        stateEstimateNoiseMatrix.setEntry(7, 2, 0.0)
        stateEstimateNoiseMatrix.setEntry(7, 3, 0.0)
        stateEstimateNoiseMatrix.setEntry(7, 4, 0.0)
        stateEstimateNoiseMatrix.setEntry(7, 5, 0.0)
        stateEstimateNoiseMatrix.setEntry(7, 6, 0.0)
        stateEstimateNoiseMatrix.setEntry(7, 7, 1.0)
        stateEstimateNoiseMatrix.setEntry(7, 8, 0.0)
        stateEstimateNoiseMatrix.setEntry(8, 0, 0.0)
        stateEstimateNoiseMatrix.setEntry(8, 1, 0.0)
        stateEstimateNoiseMatrix.setEntry(8, 2, 0.0)
        stateEstimateNoiseMatrix.setEntry(8, 3, 0.0)
        stateEstimateNoiseMatrix.setEntry(8, 4, 0.0)
        stateEstimateNoiseMatrix.setEntry(8, 5, 0.0)
        stateEstimateNoiseMatrix.setEntry(8, 6, 0.0)
        stateEstimateNoiseMatrix.setEntry(8, 7, 0.0)
        stateEstimateNoiseMatrix.setEntry(8, 8, 1.0)

        // F
        stateTransitionMatrix.setEntry(0, 0, 1.0)
        stateTransitionMatrix.setEntry(0, 1, 0.0)
        stateTransitionMatrix.setEntry(0, 2, 0.0)
        stateTransitionMatrix.setEntry(0, 3, TIME_DELTA)
        stateTransitionMatrix.setEntry(0, 4, 0.0)
        stateTransitionMatrix.setEntry(0, 5, 0.0)
        stateTransitionMatrix.setEntry(0, 6, 0.5*TIME_DELTA.pow(2))
        stateTransitionMatrix.setEntry(0, 7, 0.0)
        stateTransitionMatrix.setEntry(0, 8, 0.0)
        stateTransitionMatrix.setEntry(1, 0, 0.0)
        stateTransitionMatrix.setEntry(1, 1, 1.0)
        stateTransitionMatrix.setEntry(1, 2, 0.0)
        stateTransitionMatrix.setEntry(1, 3, 0.0)
        stateTransitionMatrix.setEntry(1, 4, TIME_DELTA)
        stateTransitionMatrix.setEntry(1, 5, 0.0)
        stateTransitionMatrix.setEntry(1, 6, 0.0)
        stateTransitionMatrix.setEntry(1, 7, 0.5*TIME_DELTA.pow(2))
        stateTransitionMatrix.setEntry(1, 8, 0.0)
        stateTransitionMatrix.setEntry(2, 0, 0.0)
        stateTransitionMatrix.setEntry(2, 1, 0.0)
        stateTransitionMatrix.setEntry(2, 2, 1.0)
        stateTransitionMatrix.setEntry(2, 3, 0.0)
        stateTransitionMatrix.setEntry(2, 4, 0.0)
        stateTransitionMatrix.setEntry(2, 5, TIME_DELTA)
        stateTransitionMatrix.setEntry(2, 6, 0.0)
        stateTransitionMatrix.setEntry(2, 7, 0.0)
        stateTransitionMatrix.setEntry(2, 8, 0.5*TIME_DELTA.pow(2))
        stateTransitionMatrix.setEntry(3, 0, 0.0)
        stateTransitionMatrix.setEntry(3, 1, 0.0)
        stateTransitionMatrix.setEntry(3, 2, 0.0)
        stateTransitionMatrix.setEntry(3, 3, 1.0)
        stateTransitionMatrix.setEntry(3, 4, 0.0)
        stateTransitionMatrix.setEntry(3, 5, 0.0)
        stateTransitionMatrix.setEntry(3, 6, TIME_DELTA)
        stateTransitionMatrix.setEntry(3, 7, 0.0)
        stateTransitionMatrix.setEntry(3, 8, 0.0)
        stateTransitionMatrix.setEntry(4, 0, 0.0)
        stateTransitionMatrix.setEntry(4, 1, 0.0)
        stateTransitionMatrix.setEntry(4, 2, 0.0)
        stateTransitionMatrix.setEntry(4, 3, 0.0)
        stateTransitionMatrix.setEntry(4, 4, 1.0)
        stateTransitionMatrix.setEntry(4, 5, 0.0)
        stateTransitionMatrix.setEntry(4, 6, 0.0)
        stateTransitionMatrix.setEntry(4, 7, TIME_DELTA)
        stateTransitionMatrix.setEntry(4, 8, 0.0)
        stateTransitionMatrix.setEntry(5, 0, 0.0)
        stateTransitionMatrix.setEntry(5, 1, 0.0)
        stateTransitionMatrix.setEntry(5, 2, 0.0)
        stateTransitionMatrix.setEntry(5, 3, 0.0)
        stateTransitionMatrix.setEntry(5, 4, 0.0)
        stateTransitionMatrix.setEntry(5, 5, 1.0)
        stateTransitionMatrix.setEntry(5, 6, 0.0)
        stateTransitionMatrix.setEntry(5, 7, 0.0)
        stateTransitionMatrix.setEntry(5, 8, TIME_DELTA)
        stateTransitionMatrix.setEntry(6, 0, 0.0)
        stateTransitionMatrix.setEntry(6, 1, 0.0)
        stateTransitionMatrix.setEntry(6, 2, 0.0)
        stateTransitionMatrix.setEntry(6, 3, 0.0)
        stateTransitionMatrix.setEntry(6, 4, 0.0)
        stateTransitionMatrix.setEntry(6, 5, 0.0)
        stateTransitionMatrix.setEntry(6, 6, 1.0)
        stateTransitionMatrix.setEntry(6, 7, 0.0)
        stateTransitionMatrix.setEntry(6, 8, 0.0)
        stateTransitionMatrix.setEntry(7, 0, 0.0)
        stateTransitionMatrix.setEntry(7, 1, 0.0)
        stateTransitionMatrix.setEntry(7, 2, 0.0)
        stateTransitionMatrix.setEntry(7, 3, 0.0)
        stateTransitionMatrix.setEntry(7, 4, 0.0)
        stateTransitionMatrix.setEntry(7, 5, 0.0)
        stateTransitionMatrix.setEntry(7, 6, 0.0)
        stateTransitionMatrix.setEntry(7, 7, 1.0)
        stateTransitionMatrix.setEntry(7, 8, 0.0)
        stateTransitionMatrix.setEntry(8, 0, 0.0)
        stateTransitionMatrix.setEntry(8, 1, 0.0)
        stateTransitionMatrix.setEntry(8, 2, 0.0)
        stateTransitionMatrix.setEntry(8, 3, 0.0)
        stateTransitionMatrix.setEntry(8, 4, 0.0)
        stateTransitionMatrix.setEntry(8, 5, 0.0)
        stateTransitionMatrix.setEntry(8, 6, 0.0)
        stateTransitionMatrix.setEntry(8, 7, 0.0)
        stateTransitionMatrix.setEntry(8, 8, 1.0)

        // B
        controlMatrix.setEntry(0, 0, 0.0)
        controlMatrix.setEntry(1, 0, 0.0)
        controlMatrix.setEntry(2, 0, 0.0)
        controlMatrix.setEntry(3, 0, 0.0)
        controlMatrix.setEntry(4, 0, 0.0)
        controlMatrix.setEntry(5, 0, 0.0)
        controlMatrix.setEntry(6, 0, 0.0)
        controlMatrix.setEntry(7, 0, 0.0)
        controlMatrix.setEntry(8, 0, 0.0)

        // Complete covariance Q
        processNoiseMatrix.setEntry(0, 0, 0.25*TIME_DELTA.pow(4))
        processNoiseMatrix.setEntry(0, 1, 0.25*TIME_DELTA.pow(4))
        processNoiseMatrix.setEntry(0, 2, 0.25*TIME_DELTA.pow(4))
        processNoiseMatrix.setEntry(0, 3, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(0, 4, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(0, 5, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(0, 6, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(0, 7, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(0, 8, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(1, 0, 0.25*TIME_DELTA.pow(4))
        processNoiseMatrix.setEntry(1, 1, 0.25*TIME_DELTA.pow(4))
        processNoiseMatrix.setEntry(1, 2, 0.25*TIME_DELTA.pow(4))
        processNoiseMatrix.setEntry(1, 3, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(1, 4, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(1, 5, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(1, 6, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(1, 7, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(1, 8, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(2, 0, 0.25*TIME_DELTA.pow(4))
        processNoiseMatrix.setEntry(2, 1, 0.25*TIME_DELTA.pow(4))
        processNoiseMatrix.setEntry(2, 2, 0.25*TIME_DELTA.pow(4))
        processNoiseMatrix.setEntry(2, 3, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(2, 4, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(2, 5, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(2, 6, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(2, 7, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(2, 8, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(3, 0, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(3, 1, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(3, 2, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(3, 3, TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(3, 4, TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(3, 5, TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(3, 6, TIME_DELTA)
        processNoiseMatrix.setEntry(3, 7, TIME_DELTA)
        processNoiseMatrix.setEntry(3, 8, TIME_DELTA)
        processNoiseMatrix.setEntry(4, 0, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(4, 1, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(4, 2, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(4, 3, TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(4, 4, TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(4, 5, TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(4, 6, TIME_DELTA)
        processNoiseMatrix.setEntry(4, 7, TIME_DELTA)
        processNoiseMatrix.setEntry(4, 8, TIME_DELTA)
        processNoiseMatrix.setEntry(5, 0, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(5, 1, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(5, 2, 0.5*TIME_DELTA.pow(3))
        processNoiseMatrix.setEntry(5, 3, TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(5, 4, TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(5, 5, TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(5, 6, TIME_DELTA)
        processNoiseMatrix.setEntry(5, 7, TIME_DELTA)
        processNoiseMatrix.setEntry(5, 8, TIME_DELTA)
        processNoiseMatrix.setEntry(6, 0, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(6, 1, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(6, 2, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(6, 3, TIME_DELTA)
        processNoiseMatrix.setEntry(6, 4, TIME_DELTA)
        processNoiseMatrix.setEntry(6, 5, TIME_DELTA)
        processNoiseMatrix.setEntry(6, 6, 1.0)
        processNoiseMatrix.setEntry(6, 7, 1.0)
        processNoiseMatrix.setEntry(6, 8, 1.0)
        processNoiseMatrix.setEntry(7, 0, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(7, 1, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(7, 2, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(7, 3, TIME_DELTA)
        processNoiseMatrix.setEntry(7, 4, TIME_DELTA)
        processNoiseMatrix.setEntry(7, 5, TIME_DELTA)
        processNoiseMatrix.setEntry(7, 6, 1.0)
        processNoiseMatrix.setEntry(7, 7, 1.0)
        processNoiseMatrix.setEntry(7, 8, 1.0)
        processNoiseMatrix.setEntry(8, 0, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(8, 1, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(8, 2, 0.5*TIME_DELTA.pow(2))
        processNoiseMatrix.setEntry(8, 3, TIME_DELTA)
        processNoiseMatrix.setEntry(8, 4, TIME_DELTA)
        processNoiseMatrix.setEntry(8, 5, TIME_DELTA)
        processNoiseMatrix.setEntry(8, 6, 1.0)
        processNoiseMatrix.setEntry(8, 7, 1.0)
        processNoiseMatrix.setEntry(8, 8, 1.0)
        processNoiseMatrix.scalarMultiply(MAX_ACCELERATION_VARIANCE)

        // Simple Q
        /*processNoiseMatrix.setEntry(0, 0, 0.0)
        processNoiseMatrix.setEntry(0, 1, 0.0)
        processNoiseMatrix.setEntry(0, 2, 0.0)
        processNoiseMatrix.setEntry(0, 3, 0.0)
        processNoiseMatrix.setEntry(0, 4, 0.0)
        processNoiseMatrix.setEntry(0, 5, 0.0)
        processNoiseMatrix.setEntry(0, 6, 0.0)
        processNoiseMatrix.setEntry(0, 7, 0.0)
        processNoiseMatrix.setEntry(0, 8, 0.0)
        processNoiseMatrix.setEntry(1, 0, 0.0)
        processNoiseMatrix.setEntry(1, 1, 0.0)
        processNoiseMatrix.setEntry(1, 2, 0.0)
        processNoiseMatrix.setEntry(1, 3, 0.0)
        processNoiseMatrix.setEntry(1, 4, 0.0)
        processNoiseMatrix.setEntry(1, 5, 0.0)
        processNoiseMatrix.setEntry(1, 6, 0.0)
        processNoiseMatrix.setEntry(1, 7, 0.0)
        processNoiseMatrix.setEntry(1, 8, 0.0)
        processNoiseMatrix.setEntry(2, 0, 0.0)
        processNoiseMatrix.setEntry(2, 1, 0.0)
        processNoiseMatrix.setEntry(2, 2, 0.0)
        processNoiseMatrix.setEntry(2, 3, 0.0)
        processNoiseMatrix.setEntry(2, 4, 0.0)
        processNoiseMatrix.setEntry(2, 5, 0.0)
        processNoiseMatrix.setEntry(2, 6, 0.0)
        processNoiseMatrix.setEntry(2, 7, 0.0)
        processNoiseMatrix.setEntry(2, 8, 0.0)
        processNoiseMatrix.setEntry(3, 0, 0.0)
        processNoiseMatrix.setEntry(3, 1, 0.0)
        processNoiseMatrix.setEntry(3, 2, 0.0)
        processNoiseMatrix.setEntry(3, 3, 0.0)
        processNoiseMatrix.setEntry(3, 4, 0.0)
        processNoiseMatrix.setEntry(3, 5, 0.0)
        processNoiseMatrix.setEntry(3, 6, 0.0)
        processNoiseMatrix.setEntry(3, 7, 0.0)
        processNoiseMatrix.setEntry(3, 8, 0.0)
        processNoiseMatrix.setEntry(4, 0, 0.0)
        processNoiseMatrix.setEntry(4, 1, 0.0)
        processNoiseMatrix.setEntry(4, 2, 0.0)
        processNoiseMatrix.setEntry(4, 3, 0.0)
        processNoiseMatrix.setEntry(4, 4, 0.0)
        processNoiseMatrix.setEntry(4, 5, 0.0)
        processNoiseMatrix.setEntry(4, 6, 0.0)
        processNoiseMatrix.setEntry(4, 7, 0.0)
        processNoiseMatrix.setEntry(4, 8, 0.0)
        processNoiseMatrix.setEntry(5, 0, 0.0)
        processNoiseMatrix.setEntry(5, 1, 0.0)
        processNoiseMatrix.setEntry(5, 2, 0.0)
        processNoiseMatrix.setEntry(5, 3, 0.0)
        processNoiseMatrix.setEntry(5, 4, 0.0)
        processNoiseMatrix.setEntry(5, 5, 0.0)
        processNoiseMatrix.setEntry(5, 6, 0.0)
        processNoiseMatrix.setEntry(5, 7, 0.0)
        processNoiseMatrix.setEntry(5, 8, 0.0)
        processNoiseMatrix.setEntry(6, 0, 0.0)
        processNoiseMatrix.setEntry(6, 1, 0.0)
        processNoiseMatrix.setEntry(6, 2, 0.0)
        processNoiseMatrix.setEntry(6, 3, 0.0)
        processNoiseMatrix.setEntry(6, 4, 0.0)
        processNoiseMatrix.setEntry(6, 5, 0.0)
        processNoiseMatrix.setEntry(6, 6, 1.0)
        processNoiseMatrix.setEntry(6, 7, 1.0)
        processNoiseMatrix.setEntry(6, 8, 1.0)
        processNoiseMatrix.setEntry(7, 0, 0.0)
        processNoiseMatrix.setEntry(7, 1, 0.0)
        processNoiseMatrix.setEntry(7, 2, 0.0)
        processNoiseMatrix.setEntry(7, 3, 0.0)
        processNoiseMatrix.setEntry(7, 4, 0.0)
        processNoiseMatrix.setEntry(7, 5, 0.0)
        processNoiseMatrix.setEntry(7, 6, 1.0)
        processNoiseMatrix.setEntry(7, 7, 1.0)
        processNoiseMatrix.setEntry(7, 8, 1.0)
        processNoiseMatrix.setEntry(8, 0, 0.0)
        processNoiseMatrix.setEntry(8, 1, 0.0)
        processNoiseMatrix.setEntry(8, 2, 0.0)
        processNoiseMatrix.setEntry(8, 3, 0.0)
        processNoiseMatrix.setEntry(8, 4, 0.0)
        processNoiseMatrix.setEntry(8, 5, 0.0)
        processNoiseMatrix.setEntry(8, 6, 1.0)
        processNoiseMatrix.setEntry(8, 7, 1.0)
        processNoiseMatrix.setEntry(8, 8, 1.0)
        processNoiseMatrix.scalarMultiply(MAX_ACCELERATION_VARIANCE)*/

        return DefaultProcessModel(stateTransitionMatrix, null, processNoiseMatrix, stateVector, stateEstimateNoiseMatrix)
    }

    private fun configureMeasurementModel(): DefaultMeasurementModel {
        // H
        measurementMatrix.setEntry(0, 0, 1.0)
        measurementMatrix.setEntry(0, 1, 0.0)
        measurementMatrix.setEntry(0, 2, 0.0)
        measurementMatrix.setEntry(0, 3, 0.0)
        measurementMatrix.setEntry(0, 4, 0.0)
        measurementMatrix.setEntry(0, 5, 0.0)
        measurementMatrix.setEntry(0, 6, 0.0)
        measurementMatrix.setEntry(0, 7, 0.0)
        measurementMatrix.setEntry(0, 8, 0.0)
        measurementMatrix.setEntry(1, 0, 0.0)
        measurementMatrix.setEntry(1, 1, 1.0)
        measurementMatrix.setEntry(1, 2, 0.0)
        measurementMatrix.setEntry(1, 3, 0.0)
        measurementMatrix.setEntry(1, 4, 0.0)
        measurementMatrix.setEntry(1, 5, 0.0)
        measurementMatrix.setEntry(1, 6, 0.0)
        measurementMatrix.setEntry(1, 7, 0.0)
        measurementMatrix.setEntry(1, 8, 0.0)
        measurementMatrix.setEntry(2, 0, 0.0)
        measurementMatrix.setEntry(2, 1, 0.0)
        measurementMatrix.setEntry(2, 2, 1.0)
        measurementMatrix.setEntry(2, 3, 0.0)
        measurementMatrix.setEntry(2, 4, 0.0)
        measurementMatrix.setEntry(2, 5, 0.0)
        measurementMatrix.setEntry(2, 6, 0.0)
        measurementMatrix.setEntry(2, 7, 0.0)
        measurementMatrix.setEntry(2, 8, 0.0)
        measurementMatrix.setEntry(3, 0, 0.0)
        measurementMatrix.setEntry(3, 1, 0.0)
        measurementMatrix.setEntry(3, 2, 0.0)
        measurementMatrix.setEntry(3, 3, 0.0)
        measurementMatrix.setEntry(3, 4, 0.0)
        measurementMatrix.setEntry(3, 5, 0.0)
        measurementMatrix.setEntry(3, 6, 1.0)
        measurementMatrix.setEntry(3, 7, 0.0)
        measurementMatrix.setEntry(3, 8, 0.0)
        measurementMatrix.setEntry(4, 0, 0.0)
        measurementMatrix.setEntry(4, 1, 0.0)
        measurementMatrix.setEntry(4, 2, 0.0)
        measurementMatrix.setEntry(4, 3, 0.0)
        measurementMatrix.setEntry(4, 4, 0.0)
        measurementMatrix.setEntry(4, 5, 0.0)
        measurementMatrix.setEntry(4, 6, 0.0)
        measurementMatrix.setEntry(4, 7, 1.0)
        measurementMatrix.setEntry(4, 8, 0.0)
        measurementMatrix.setEntry(5, 0, 0.0)
        measurementMatrix.setEntry(5, 1, 0.0)
        measurementMatrix.setEntry(5, 2, 0.0)
        measurementMatrix.setEntry(5, 3, 0.0)
        measurementMatrix.setEntry(5, 4, 0.0)
        measurementMatrix.setEntry(5, 5, 0.0)
        measurementMatrix.setEntry(5, 6, 0.0)
        measurementMatrix.setEntry(5, 7, 0.0)
        measurementMatrix.setEntry(5, 8, 1.0)

        // Simple diagonal R
        /*measurementNoiseMatrix.setEntry(0, 0, 0.005)
        measurementNoiseMatrix.setEntry(0, 1, 0.0)
        measurementNoiseMatrix.setEntry(0, 2, 0.0)
        measurementNoiseMatrix.setEntry(0, 3, 0.0)
        measurementNoiseMatrix.setEntry(0, 4, 0.0)
        measurementNoiseMatrix.setEntry(0, 5, 0.0)
        measurementNoiseMatrix.setEntry(1, 0, 0.0)
        measurementNoiseMatrix.setEntry(1, 1, 0.0137)
        measurementNoiseMatrix.setEntry(1, 2, 0.0)
        measurementNoiseMatrix.setEntry(1, 3, 0.0)
        measurementNoiseMatrix.setEntry(1, 4, 0.0)
        measurementNoiseMatrix.setEntry(1, 5, 0.0)
        measurementNoiseMatrix.setEntry(2, 0, 0.0)
        measurementNoiseMatrix.setEntry(2, 1, 0.0)
        measurementNoiseMatrix.setEntry(2, 2, 0.029)
        measurementNoiseMatrix.setEntry(2, 3, 0.0)
        measurementNoiseMatrix.setEntry(2, 4, 0.0)
        measurementNoiseMatrix.setEntry(2, 5, 0.0)
        measurementNoiseMatrix.setEntry(3, 0, 0.0)
        measurementNoiseMatrix.setEntry(3, 1, 0.0)
        measurementNoiseMatrix.setEntry(3, 2, 0.0)
        measurementNoiseMatrix.setEntry(3, 3, 0.00001)
        measurementNoiseMatrix.setEntry(3, 4, 0.0)
        measurementNoiseMatrix.setEntry(3, 5, 0.0)
        measurementNoiseMatrix.setEntry(4, 0, 0.0)
        measurementNoiseMatrix.setEntry(4, 1, 0.0)
        measurementNoiseMatrix.setEntry(4, 2, 0.0)
        measurementNoiseMatrix.setEntry(4, 3, 0.0)
        measurementNoiseMatrix.setEntry(4, 4, 0.00001)
        measurementNoiseMatrix.setEntry(4, 5, 0.0)
        measurementNoiseMatrix.setEntry(5, 0, 0.0)
        measurementNoiseMatrix.setEntry(5, 1, 0.0)
        measurementNoiseMatrix.setEntry(5, 2, 0.0)
        measurementNoiseMatrix.setEntry(5, 3, 0.0)
        measurementNoiseMatrix.setEntry(5, 4, 0.0)
        measurementNoiseMatrix.setEntry(5, 5, 0.00001)*/

        // Complete Covariance R
        measurementNoiseMatrix.setEntry(0, 0, 0.005)
        measurementNoiseMatrix.setEntry(0, 1, 0.0023)
        measurementNoiseMatrix.setEntry(0, 2, 0.0018)
        measurementNoiseMatrix.setEntry(0, 3, 0.0)
        measurementNoiseMatrix.setEntry(0, 4, 0.0)
        measurementNoiseMatrix.setEntry(0, 5, 0.0)
        measurementNoiseMatrix.setEntry(1, 0, 0.0023)
        measurementNoiseMatrix.setEntry(1, 1, 0.0137)
        measurementNoiseMatrix.setEntry(1, 2, 0.0036)
        measurementNoiseMatrix.setEntry(1, 3, 0.0)
        measurementNoiseMatrix.setEntry(1, 4, 0.0)
        measurementNoiseMatrix.setEntry(1, 5, 0.0)
        measurementNoiseMatrix.setEntry(2, 0, 0.0018)
        measurementNoiseMatrix.setEntry(2, 1, 0.0036)
        measurementNoiseMatrix.setEntry(2, 2, 0.029)
        measurementNoiseMatrix.setEntry(2, 3, 0.0)
        measurementNoiseMatrix.setEntry(2, 4, 0.0)
        measurementNoiseMatrix.setEntry(2, 5, 0.0)
        measurementNoiseMatrix.setEntry(3, 0, 0.0)
        measurementNoiseMatrix.setEntry(3, 1, 0.0)
        measurementNoiseMatrix.setEntry(3, 2, 0.0)
        measurementNoiseMatrix.setEntry(3, 3, 0.05)
        measurementNoiseMatrix.setEntry(3, 4, 0.001)
        measurementNoiseMatrix.setEntry(3, 5, 0.001)
        measurementNoiseMatrix.setEntry(4, 0, 0.0)
        measurementNoiseMatrix.setEntry(4, 1, 0.0)
        measurementNoiseMatrix.setEntry(4, 2, 0.0)
        measurementNoiseMatrix.setEntry(4, 3, 0.001)
        measurementNoiseMatrix.setEntry(4, 4, 0.05)
        measurementNoiseMatrix.setEntry(4, 5, 0.001)
        measurementNoiseMatrix.setEntry(5, 0, 0.0)
        measurementNoiseMatrix.setEntry(5, 1, 0.0)
        measurementNoiseMatrix.setEntry(5, 2, 0.0)
        measurementNoiseMatrix.setEntry(5, 3, 0.01)
        measurementNoiseMatrix.setEntry(5, 4, 0.01)
        measurementNoiseMatrix.setEntry(5, 5, 0.2)

        return DefaultMeasurementModel(measurementMatrix, measurementNoiseMatrix)
    }
}