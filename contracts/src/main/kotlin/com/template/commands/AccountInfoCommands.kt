package com.template.commands

import net.corda.core.contracts.CommandData


/** Commands to be used in conjunction with [AccountInfo] states. */
interface AccountCommand : CommandData

/** For use when creating an [AccountInfo] for a new account. */
class Create : AccountCommand
