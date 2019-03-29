package dev.komu.ahwen.planner

import dev.komu.ahwen.metadata.MetadataManager
import dev.komu.ahwen.parse.QueryData
import dev.komu.ahwen.query.*
import dev.komu.ahwen.tx.Transaction

class BasicQueryPlanner(
    private val planner: Planner,
    private val metadataManager: MetadataManager) : QueryPlanner {

    override fun createPlan(data: QueryData, tx: Transaction): Plan {
        val plans = data.tables.map { tableName ->
            val view = metadataManager.getViewDef(tableName, tx)
            if (view != null)
                planner.createQueryPlan(view, tx)
            else
                TablePlan(tableName, metadataManager, tx)
        }

        val product = plans.reduce(::ProductPlan)
        val select = SelectPlan(product, data.predicate)
        return ProjectPlan(select, data.fields)
    }
}
