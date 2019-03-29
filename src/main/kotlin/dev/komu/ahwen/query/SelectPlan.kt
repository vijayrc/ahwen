package dev.komu.ahwen.query

class SelectPlan(private val plan: Plan, private val predicate: Predicate) : Plan by plan {

    override fun open(): Scan =
        SelectScan(plan.open(), predicate)

    override val recordsOutput: Int
        get() = plan.recordsOutput / predicate.reductionFactor(plan)

    override fun distinctValues(fieldName: String): Int =
        if (predicate.equatesWithConstant(fieldName) != null)
            1
        else
            minOf(plan.distinctValues(fieldName), recordsOutput)
}
