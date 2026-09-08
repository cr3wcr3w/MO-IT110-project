$ErrorActionPreference = "Stop"

$RootDir = Resolve-Path (Join-Path $PSScriptRoot "..")
$AppJob = $null
$LastFingerprint = ""

function Test-IsIgnoredPath {
    param([string] $Path)

    $ignoredDirectories = @(
        (Join-Path $RootDir ".git"),
        (Join-Path $RootDir ".gradle"),
        (Join-Path $RootDir "build"),
        (Join-Path $RootDir "app\build"),
        (Join-Path $RootDir "app\bin")
    )

    foreach ($ignoredDirectory in $ignoredDirectories) {
        if ($Path.StartsWith($ignoredDirectory, [System.StringComparison]::OrdinalIgnoreCase)) {
            return $true
        }
    }

    return $false
}

function Get-ProjectFingerprint {
    Get-ChildItem -Path $RootDir -Recurse -File -Force |
        Where-Object { -not (Test-IsIgnoredPath $_.FullName) } |
        Sort-Object FullName |
        ForEach-Object { "$($_.FullName)|$($_.LastWriteTimeUtc.Ticks)|$($_.Length)" } |
        Out-String
}

function Stop-App {
    if ($null -ne $script:AppJob) {
        Stop-Job -Job $script:AppJob -ErrorAction SilentlyContinue
        Remove-Job -Job $script:AppJob -Force -ErrorAction SilentlyContinue
        $script:AppJob = $null
    }
}

function Start-App {
    Stop-App
    Write-Host "[dev-reload] starting app..."

    $script:AppJob = Start-Job -ScriptBlock {
        param([string] $ProjectRoot)

        Set-Location $ProjectRoot
        & .\gradlew.bat run
    } -ArgumentList $RootDir
}

try {
    $LastFingerprint = Get-ProjectFingerprint
    Start-App

    while ($true) {
        Start-Sleep -Seconds 1

        if ($null -ne $AppJob) {
            Receive-Job -Job $AppJob
        }

        $currentFingerprint = Get-ProjectFingerprint

        if ($currentFingerprint -ne $LastFingerprint) {
            $LastFingerprint = $currentFingerprint
            Write-Host "[dev-reload] file change detected, restarting app..."
            Start-App
        }
    }
}
finally {
    Stop-App
}
