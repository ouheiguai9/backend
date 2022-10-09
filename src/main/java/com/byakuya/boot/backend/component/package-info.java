/**
 * Created by 田伯光 at 2022/10/10 0:58
 */
@GenericGenerators({
        @GenericGenerator(name = "snowflake_id", strategy = "com.byakuya.boot.backend.component.SnowIdGenerator"),
        @GenericGenerator(name = "table_id", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
                @Parameter(name = TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY, value = "true"),
                @Parameter(name = TableGenerator.TABLE_PARAM, value = "T_SYS_TABLE_SEQUENCE"),
                @Parameter(name = TableGenerator.INITIAL_PARAM, value = "10000"),
                @Parameter(name = TableGenerator.INCREMENT_PARAM, value = "20"),
        })
})
package com.byakuya.boot.backend.component;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.GenericGenerators;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;