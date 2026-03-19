package dp;

import java.util.Arrays;

/**
 * 给你一个整数数组 nums ，找到其中最长严格递增子序列的长度。
 */
public class LongestIncreasingSubsequence {

    public static int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        // 定义 dp 数组，dp[i] 表示以 nums[i] 结尾的最长递增子序列的长度
        int[] dp = new int[nums.length];

        // 1. 初始化：每个单独的数字本身就是一个长度为 1 的子序列
        Arrays.fill(dp, 1);

        // 记录全局的最长子序列长度
        int maxLen = 1;

        // 2. 状态转移：从左到右依次计算每个位置的 dp 值
        for (int i = 1; i < nums.length; i++) {
            // 往回看，遍历当前数字 i 之前的所有数字 j
            for (int j = 0; j < i; j++) {
                // 如果当前数字 nums[i] 大于前面的数字 nums[j]，说明可以接在它后面
                if (nums[i] > nums[j]) {
                    // 核心状态转移方程：
                    // 到底接在谁后面能变得最长？取其中的最大值。
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            // 每计算完一个 dp[i]，就去挑战一下全局最大值记录
            maxLen = Math.max(maxLen, dp[i]);
        }

        // 打印最终的 dp 数组，方便对比推演过程
        System.out.println("当前 nums 数组: " + Arrays.toString(nums));
        System.out.println("计算出的 dp 数组: " + Arrays.toString(dp));

        return maxLen;
    }

    public static void main(String[] args) {
        // 这是你修改后的数组，把最后一个 7 变成了 19
        int[] nums = {10, 9, 2, 5, 3, 7, 101, 18, 19};

        int result = lengthOfLIS(nums);
        System.out.println("最长递增子序列的最大长度是: " + result);
    }
}
