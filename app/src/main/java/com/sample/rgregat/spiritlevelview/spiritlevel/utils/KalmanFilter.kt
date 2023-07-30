package com.sample.rgregat.spiritlevelview.spiritlevel.utils

import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.kotlin.transpose
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Variables:
 * - x:  This array (floatArrayOf) contains the state vector of the Kalman filter.
 *       In case of the given Kalman filter, the values for pitch, gyro bias for pitch and roll
 *       are stored in it.
 * - P:  This matrix represents the estimation covariance of the filter.
 *       It contains information about the uncertainty of the state vector.
 *
 * Variables in detail:
 * Array x:
 * - Index 0: This index contains the value for the pitch. It represents the current
 *            estimate of the pitch angle.
 * - Index 1: This index contains the value for the gyro bias for pitch.
 *            It represents the error or deviation of the gyroscope in pitch.
 * - Index 2: This index contains the value for the roll.
 *            It represents the current estimate of the roll angle.
 *
 * Matrix P:
 * - P is a 3x3 matrix representing the estimation covariance of the filter.
 *   It contains information about the uncertainty or precision of the estimation for
 *   each state in the state vector x.
 * - P[0][0]: This element represents the uncertainty related to the estimated
 *            pitch angle.
 * - P[0][1]: This element represents the correlation between the estimated
 *            pitch angle and the gyro bias for pitch.
 * - P[0][2]: This element represents the correlation between the estimated
 *            pitch angle and the estimated roll angle.
 * - P[1][0]: This element represents the correlation between the gyro bias
 *            for Pitch and the estimated pitch angle.
 * - P[1][1]: This element represents the uncertainty with respect to the
 *            estimated gyro bias for pitch.
 * - P[1][2]: This element represents the correlation between the gyro bias
 *            for Pitch and the estimated roll angle.
 * - P[2][0]: This element represents the correlation between the estimated
 *            roll angle and the estimated pitch angle.
 * - P[2][1]: This element represents the correlation between the estimated
 *            roll angle and the gyro bias for pitch.
 * - P[2][2]: This element represents the uncertainty with respect to the
 *            estimated roll angle.
 *
 * The matrix P is an important component of the Kalman filter because it represents
 * the uncertainty of the estimates. It is updated by the Kalman filter,
 * to account for the uncertainty based on the measurements.
 *
 *  Functions:
 *  - processAccelData(accelValues: FloatArray): This function processes the acceleration
 *          data and updates the state vector and the estimation covariance of the filter based on
 *          the measured values. It uses the acceleration values (accelValues) to calculate the
 *          deviation (innovation) and update the state vector and the estimation covariance using
 *          the Kalman filter.
 *  - processGyroData(gyroValues: FloatArray): This function processes the gyroscope
 *          data and updates the state vector of the filter based on the measured values. It uses
 *          the gyroscope values (gyroValues) to update the state vector.
 *  - getOrientation(): This function returns the current state vector of the filter,
 *          which contains the calculated values for pitch, gyro bias for pitch and roll.
 *          The state vector can be used for further processing and use of the calculated values.
 *
 *  The implementation of the Kalman filter is based on the basic equations of the
 *  Kalman filter, such as the state transition matrix, the measurement matrix,
 *  the process noise covariance and the measurement noise covariance. These values
 *  are specified in the implementation and can be adjusted depending on the application
 *  to achieve optimal performance of the filter.
 *
 *  Weitere Matrizen:
 *
 *  - Matrix H
 *    - The matrix H represents the measurement matrix, which indicates the relationship
 *      between the measured values and the state vector x.
 *    - In the given implementation, the matrix H is a 2x3 matrix, since we use the
 *      acceleration values for the calculation of pitch and roll.
 *    - The values in the matrix H are as follows:
 *      - H[0][0] = 1.0f: This element shows the weighting of the Pitch estimate (x[0])
 *                        with respect to the acceleration measurement.
 *      - H[1][2] = 1.0f: This element shows the weighting of the roll estimate (x[2])
 *                        with respect to the acceleration measurement.
 *      - All other elements in H are 0.
 *
 *  - Matrix R
 *    - The matrix R represents the measurement noise covariance, which reflects the
 *      uncertainty of the measurements.
 *    - In the given implementation, the matrix R is a 2x2 matrix, since we have two
 *      measurements (pitch and roll).
 *    - The values in the matrix R are as follows:
 *      - R[0][0] = 0.01f: This element represents the uncertainty of the pitch measurement.
 *      - R[1][1] = 0.01f: This element represents the uncertainty of the roll measurement.
 *      - All other elements in R are 0.
 *
 *  - Matrix S
 *    - The matrix S represents the innovation covariance and is used to quantify the
 *      deviation between  measurements and the predictions of the filter.
 *    - In the given implementation, the matrix S is a 2x2 matrix, since we have two
 *      measurements (pitch and roll).
 *    - The values in the matrix S are calculated in the processAccelData()
 *      function and vary depending on the current estimates and the values in the matrix P.
 *
 *  - Matrix I
 *    - The matrix I, often called the "identity matrix", is a special square matrix
 *      with dimensions nxn, where n is the number of states in the Kalman filter.
 *      In the given implementation, the matrix I has the dimension 3x3, since we have
 *      three states (pitch, gyro bias for pitch and roll).
 *    - The identity matrix I is a matrix where all diagonal elements have the value 1
 *      and all non-diagonal elements have the value 0. This means that the matrix I neutralizes
 *      the multiplication with another matrix and leaves this matrix unchanged. In the context
 *      of the Kalman filter, matrix I is used to update the estimation covariance matrix P to be updated.
 */

class KalmanFilter {
    private var x = doubleArrayOf(0.0, 0.0, 0.0) // State vector
    private var P = DMatrixRMaj(3, 3) // Estimate covariance
    private val Q = DMatrixRMaj(3, 3)

    private val H = DMatrixRMaj(2, 3) // Measurement matrix
    private val R = DMatrixRMaj(2, 2) // Measurement noise covariance
    private var Ht = DMatrixRMaj(3, 2)
    private val PHt = DMatrixRMaj(3, 2)
    private val HPHt = DMatrixRMaj(2, 2)
    private val Si = DMatrixRMaj(2, 2)
    private val K = DMatrixRMaj(3, 2)
    private val KH = DMatrixRMaj(3, 2)
    private val I = DMatrixRMaj(3, 3)

    init {
        // Initialize state vector
        x[0] = 0.0 // Pitch
        x[1] = 0.0 // Gyro bias for pitch
        x[2] = 0.0 // Roll

        // Initialize estimate covariance
        CommonOps_DDRM.fill(P, 0.0)
        P[0] = 0.1
        P[4] = 0.1
        P[8] = 0.1

        // Adjust these values to optimize the KalmanFilter
        Q[0] = 0.01 // Process noise covariance for pitch state transition.
        Q[4] = 0.02 // Process noise covariance for the state transition of gyro bias for pitch
        Q[8] = 0.01 // Process noise covariance for the state transition of roll

        // Initialize measurement matrix
        H[0] = 1.0
        H[5] = 1.0

        // Initialize measurement noise covariance
        // Adjust these values to optimize the KalmanFilter
        CommonOps_DDRM.fill(R, 0.0)
        R[0] = 0.01 // 0.01
        R[3] = 0.02 // 0.01

        // Initialize transpose of measurement matrix
        Ht = H.transpose()

        // Initialize other matrices
        CommonOps_DDRM.fill(I, 0.0)
        I[0] = 1.0
        I[4] = 1.0
        I[8] = 1.0
    }

    fun processAccelData(accelValues: FloatArray) {
        val accPitch = atan2(accelValues[1], accelValues[2])
        val accRoll = atan2(
            -accelValues[0],
            sqrt(accelValues[1] * accelValues[1] + accelValues[2] * accelValues[2])
        )

        // Innovation or measurement residual
        val y = doubleArrayOf(accPitch - x[0], accRoll - x[2])

        CommonOps_DDRM.mult(P, Ht, PHt)

        CommonOps_DDRM.mult(H, PHt, HPHt)
        CommonOps_DDRM.addEquals(HPHt, R)
        CommonOps_DDRM.invert(HPHt, Si)

        // Calculate the Kalman Gain
        CommonOps_DDRM.mult(PHt, Si, K)

        // Update state vector
        x[0] += K[0] * y[0] + K[1] * y[1]
        x[1] += K[2] * y[0] + K[3] * y[1]
        x[2] += K[4] * y[0] + K[5] * y[1]

        // Update estimate covariance
        CommonOps_DDRM.mult(K, H, KH)
        CommonOps_DDRM.subtractEquals(I, KH)

        val numRows = I.numRows
        val numCols = I.numCols
        val pArray = Array(numRows) { DoubleArray(numCols) }
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                pArray[i][j] = I[i * numCols + j]
            }
        }

        P.set(pArray)
    }

    fun processGyroData(gyroValues: FloatArray, dt: Long) {
        val gyroPitch = gyroValues[0].toDouble() * dt
        val gyroRoll = gyroValues[1].toDouble() * dt

        // Update state vector
        x[0] += gyroPitch
        x[2] += gyroRoll

        // Adjust the P matrix based on the process noise covariance Q
        CommonOps_DDRM.addEquals(P, Q)
    }

    fun getOrientation(): DoubleArray {
        return doubleArrayOf(x[0], x[1], x[2])
    }
}