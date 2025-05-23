/*
 * Copyright 2023 Ant Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alipay.antchain.bridge.relayer.commons.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlockchainDistributedTaskTypeEnum {

    ANCHOR_TASK("anchor"),

    VALIDATION_TASK("validation"),

    PROCESS_TASK("process"),

    COMMIT_TASK("committer"),

    DEPLOY_SERVICE_TASK("deployService"),

    ARCHIVE_TASK("archive"),

    AM_CONFIRM_TASK("amConfirm"),

    RELIABLE_RELAY_TASK("reliableRelay");

    @EnumValue
    private final String code;
}
