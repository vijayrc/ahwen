package dev.komu.ahwen.query.materialize

import dev.komu.ahwen.query.Plan
import dev.komu.ahwen.query.Scan
import dev.komu.ahwen.tx.Transaction
import dev.komu.ahwen.types.ColumnName

class MergeJoinPlan(
    p1: Plan,
    p2: Plan,
    private val fieldName1: ColumnName,
    private val fieldName2: ColumnName,
    tx: Transaction
) : Plan {

    private val p1 = SortPlan(p1, listOf(fieldName1), tx)
    private val p2 = SortPlan(p2, listOf(fieldName2), tx)

    override val schema = p1.schema + p2.schema

    override fun open(): Scan {
        val s1 = p1.open()
        val s2 = p2.open()

        return MergeJoinScan(s1, s2, fieldName1, fieldName2)
    }

    override val blocksAccessed: Int
        get() = p1.blocksAccessed + p2.blocksAccessed

    override val recordsOutput: Int
        get() {
            val maxVals = maxOf(p1.distinctValues(fieldName1), p2.distinctValues(fieldName2))
            return (p1.recordsOutput * p2.recordsOutput) / maxVals
        }

    override fun distinctValues(column: ColumnName): Int =
        if (column in p1.schema)
            p1.distinctValues(column)
        else
            p2.distinctValues(column)
}