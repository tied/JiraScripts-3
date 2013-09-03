/**
 *
 * A scripted field that calculates the number of days this issue was in a given status.
 * This script will tally up the total days from multiple visits to the same status.
 * NOTE: It doesn't appear you can access and total other scripted fields from a scripted field,
 * so we redo all of the math here necessary to calculate cycle time
 *
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.history.ChangeItemBean

/* For Logging
import org.apache.log4j.Category

def Category log = Category.getInstance("com.onresolve.jira.groovy.PostFunction")
log.setLevel(org.apache.log4j.Level.DEBUG)
log.debug "debug statements"
*/

/* Enable this for debugging in the admin console
def issueManager = ComponentAccessor.getIssueManager()
def issue = issueManager.getIssueObject("DT-1")
*/

def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()

def statusNames = ["In Progress","In Code Review","Ready to Test","In QA"]
def daysInStatus = [0]
def cycleTime = 0

for (statusName in statusNames) {
    changeHistoryManager.getChangeItemsForField (issue, "status").reverse().each {ChangeItemBean item ->

        def timeDiff = System.currentTimeMillis() - item.created.getTime()
        if (item.fromString == statusName) {
            daysInStatus << -timeDiff
        }
        if (item.toString == statusName){
            daysInStatus << timeDiff
        }
    }
    cycleTime += Math.round((daysInStatus.sum() / 86400000)*4)/4 as Double
    daysInStatus = [0]
}

cycleTime