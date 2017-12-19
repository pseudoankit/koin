package org.koin.test.core

import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertScopeParent

class DSLContextTest : AbstractKoinTest() {

    val FlatContextsModule = applicationContext {

        provide { ComponentA() }

        context(name = "B") {
            provide { ComponentB() }
        }

        context(name = "C") {
            provide { ComponentC() }
        }
    }

    val HierarchyContextsModule = applicationContext {
        context(name = "A") {
            provide { ComponentA() }

            context(name = "B") {
                provide { ComponentB() }

                context(name = "C") {
                    provide { ComponentC() }
                }
            }
        }
    }

    class ComponentA
    class ComponentB
    class ComponentC

    @Test
    fun `can create flat contexts`() {
        startKoin(listOf(FlatContextsModule))

        assertContexts(3)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", Scope.ROOT)
        assertScopeParent("C", Scope.ROOT)
    }

    @Test
    fun `can create hierarchic contexts`() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("A", Scope.ROOT)
        assertScopeParent("B", "A")
        assertScopeParent("C", "B")
    }

}